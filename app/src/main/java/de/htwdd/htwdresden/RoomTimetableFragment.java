package de.htwdd.htwdresden;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import de.htwdd.htwdresden.adapter.RoomTimetableAdapter;
import de.htwdd.htwdresden.classes.ConnectionHelper;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.TimetableRoomHelper;
import de.htwdd.htwdresden.service.TimetableRoomSyncService;
import de.htwdd.htwdresden.types.LessonRoom;
import io.realm.Realm;


/**
 * Übersicht über alle gespeicherten Räume und ihren Belegungen
 *
 * @author Kay Förster
 */
public class RoomTimetableFragment extends Fragment {
    private static final String LOG_TAG = "RoomTimetableFragment";
    private View mLayout;
    private Realm realm;
    private RoomTimetableAdapter roomTimetableAdapter;
    private ResponseReceiver responseReceiver;

    public RoomTimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // Aktualisieren der aktuellen Stunden-Markierung
        roomTimetableAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_room_timetable, container, false);
        realm = Realm.getDefaultInstance();
        final SwipeRefreshLayout swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);

        // Adapter für Liste erzeugen
        roomTimetableAdapter = new RoomTimetableAdapter(realm, realm.where(LessonRoom.class).distinct(Const.database.LessonRoom.ROOM).findAll());

        // ListView
        final ListView listView = mLayout.findViewById(R.id.listView);
        listView.setAdapter(roomTimetableAdapter);
        listView.addFooterView(inflater.inflate(R.layout.fragment_room_timetable_footer, listView, false), null, false);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            final LessonRoom lessonRoom = roomTimetableAdapter.getItem(i);
            if (lessonRoom == null) {
                return;
            }

            final Bundle bundle = new Bundle();
            final Intent intent = new Intent(getActivity(), RoomTimetableDetailsActivity.class);
            bundle.putString(Const.BundleParams.ROOM_TIMETABLE_ROOM, lessonRoom.getRoom());
            intent.putExtras(bundle);
            startActivity(intent);
        });
        listView.setEmptyView(mLayout.findViewById(R.id.info));

        // FloatingActionButton Aktion setzen
        mLayout.findViewById(R.id.fab_add).setOnClickListener(view -> new AlertDialog.Builder(getActivity())
                .setTitle(R.string.room_timetable_add)
                .setMessage(R.string.room_timetable_addDialog_message)
                .setView(R.layout.timetable_room_input)
                .setPositiveButton(R.string.general_add, (dialogInterface, i) -> {
                    final String room = ((EditText) ((AlertDialog) dialogInterface).findViewById(R.id.textView)).getText().toString();

                    if (room.isEmpty())
                        Toast.makeText(mLayout.getContext(), R.string.room_timetable_addDialog_message, Toast.LENGTH_LONG).show();
                    else {
                        startUpdateService(room);
                    }
                })
                .setNegativeButton(R.string.general_close, (dialogInterface, i) -> dialogInterface.cancel())
                .show());

        // SwipeRefresh Action setzen
        swipeRefreshLayout.setOnRefreshListener(() -> {
            final Context context = mLayout.getContext();

            // Überprüfe Internetverbindung
            if (ConnectionHelper.checkNoInternetConnection(context)) {
                // Meldung anzeigen
                Toast.makeText(context, R.string.info_no_internet, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
            // Überprüfe ob Räume vorhanden sind
            else if (roomTimetableAdapter.getCount() == 0) {
                Toast.makeText(context, R.string.room_timetable_no_rooms, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            } else {
                // Service starten
                Log.d(LOG_TAG, "Starte Service");
                TimetableRoomHelper.startSyncService(context);
            }
        });

        // Löschen über Context-Menu
        registerForContextMenu(listView);

        // IntentReceiver erstellen
        final IntentFilter intentFilter = new IntentFilter(Const.IntentParams.BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.addCategory(Const.IntentParams.BROADCAST_FINISH_TIMETABLE_UPDATE);
        responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(mLayout.getContext()).registerReceiver(responseReceiver, intentFilter);

        return mLayout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mLayout.getContext()).unregisterReceiver(responseReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.context_menu_room_timetable, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull final MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final LessonRoom room = roomTimetableAdapter.getItem(info.position);
        if (room == null) {
            return false;
        }

        switch (item.getItemId()) {
            case R.id.room_timetable_delete:
                realm.beginTransaction();
                realm.where(LessonRoom.class).equalTo(Const.database.LessonRoom.ROOM, room.getRoom()).findAll().deleteAllFromRealm();
                realm.commitTransaction();
                return true;
            case R.id.room_timetable_update:
                startUpdateService(room.getRoom());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Startet Service zum aktualisieren des Belegungsplanes
     *
     * @param room optionale Raumbezeichnung, wenn angeben wird nur dieser aktualisiert
     */
    private void startUpdateService(@Nullable final String room) {
        final Context context = mLayout.getContext();
        final Intent intent = new Intent(context, TimetableRoomSyncService.class);

        // Raum übergeben
        if (room != null && !room.isEmpty()) {
            intent.putExtra(Const.BundleParams.ROOM_TIMETABLE_ROOM, room);
        }

        // Service starten
        Log.d(LOG_TAG, "Starte Service");
        context.startService(intent);
        ((SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout)).setRefreshing(true);
    }

    /**
     * Empfänger für Updates der Noten vom Service
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final SwipeRefreshLayout swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));

            final int intentResponse = intent.getIntExtra(Const.IntentParams.BROADCAST_CODE, -1);
            switch (intentResponse) {
                case 0:
                    Toast.makeText(context, R.string.room_timetable_update_success, Toast.LENGTH_LONG).show();
                    break;
                case 404:
                    Toast.makeText(context, R.string.room_timetable_add_no_Lessons, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    if (intent.hasExtra(Const.IntentParams.BROADCAST_MESSAGE)) {
                        Toast.makeText(context, intent.getStringExtra(Const.IntentParams.BROADCAST_MESSAGE), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, R.string.info_error, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }
}
