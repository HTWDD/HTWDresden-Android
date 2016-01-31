package de.htwdd.htwdresden;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.htwdd.htwdresden.adapter.RoomTimetableAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.classes.LessonHelper;
import de.htwdd.htwdresden.classes.VolleyDownloader;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableRoomDAO;
import de.htwdd.htwdresden.events.UpdateTimetableEvent;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.Lesson;
import de.htwdd.htwdresden.types.RoomTimetable;


/**
 * Übersicht über alle gespeicherten Räume und ihren Belegungen
 *
 * @author Kay Förster
 */
public class RoomTimetableFragment extends Fragment {
    private View mLayout;
    private ArrayList<RoomTimetable> roomTimetables = new ArrayList<>();
    private RoomTimetableAdapter roomTimetableAdapter;

    public RoomTimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_room_timetable, container, false);

        // Setze Toolbartitle
        ((INavigation) getActivity()).setTitle(getResources().getString(R.string.navi_room_timetable));

        // Adapter für Liste erzeugen
        roomTimetableAdapter = new RoomTimetableAdapter(getActivity(), roomTimetables);

        // ListView
        ListView listView = (ListView) mLayout.findViewById(R.id.listView);
        listView.setAdapter(roomTimetableAdapter);
        listView.addFooterView(inflater.inflate(R.layout.fragment_room_timetable_footer, listView, false), null, false);

        // Daten zum anzeigen laden
        loadData();

        // FloatingActionButton Aktion setzen
        FloatingActionButton floatingActionButton = (FloatingActionButton) mLayout.findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.room_timetable_add)
                        .setMessage(R.string.room_timetable_addDialog_message)
                        .setView(R.layout.timetable_room_input)
                        .setPositiveButton(R.string.general_add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextView textView = (EditText) ((AlertDialog) dialogInterface).findViewById(R.id.textView);
                                loadRoom(textView.getText().toString());
                            }
                        })
                        .setNegativeButton(R.string.general_close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });

        // Löschen über Context-Menu
        registerForContextMenu(listView);

        return mLayout;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_room_timetable, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String room = roomTimetables.get(info.position).roomName;

        switch (item.getItemId()) {
            case R.id.room_timetable_delete:
                // Lösche Raum aus DB
                DatabaseManager databaseManager = new DatabaseManager(getActivity());
                TimetableRoomDAO timetableRoomDAO = new TimetableRoomDAO(databaseManager);
                timetableRoomDAO.deleteRoom(room);
                loadData();
                return true;
            case R.id.room_timetable_update:
                loadRoom(room);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Zeigt die Raumpläne aus der Datenbank in der ListView an
     */
    private void loadData() {
        // Kalender zum bestimmen welcher Plan angezeigt wird
        Calendar calendar = GregorianCalendar.getInstance();

        // Wenn Sonntag ist, auf Plan für Montag springen
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            calendar.add(Calendar.DAY_OF_YEAR, 1);

        DatabaseManager databaseManager = new DatabaseManager(getActivity());
        TimetableRoomDAO timetableRoomDAO = new TimetableRoomDAO(databaseManager);

        // Lade Stundepläne aus DB
        roomTimetables.clear();
        roomTimetables.addAll(timetableRoomDAO.getOverview(calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.WEEK_OF_YEAR)));

        // Hinweis ein / ausblenden
        TextView textView = (TextView) mLayout.findViewById(R.id.info);
        ListView listView = (ListView) mLayout.findViewById(R.id.listView);

        if (roomTimetables.size() == 0) {
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        roomTimetableAdapter.notifyDataSetChanged();
    }

    /**
     * Lädt den gesuchten Raum vom Webservice uns speichert diesen in der Datenbank
     *
     * @param room für welchen der Belegungsplan geladen werden soll
     */
    private void loadRoom(@NonNull final String room) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Bestimme Fehlermeldung
                int responseCode = VolleyDownloader.getResponseCode(error);

                // Fehlermeldung anzeigen
                String message;
                switch (responseCode) {
                    case Const.internet.HTTP_TIMEOUT:
                        message = getString(R.string.info_internet_timeout);
                        break;
                    case Const.internet.HTTP_NO_CONNECTION:
                    case Const.internet.HTTP_NOT_FOUND:
                        message = getString(R.string.info_internet_no_connection);
                        break;
                    case Const.internet.HTTP_NETWORK_ERROR:
                    default:
                        message = getString(R.string.info_internet_error);
                }
                Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG).setAction(R.string.general_repeat, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadRoom(room);
                    }
                }).show();

                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<Lesson> lessons;
                try {
                    lessons = LessonHelper.getList(response);
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), "[Fehler] beim Parsen: Daten: " + response);
                    Log.e(this.getClass().getSimpleName(), e.toString());

                    // Fehlermeldung anzeigen
                    Toast.makeText(getActivity(), R.string.info_error_parse, Toast.LENGTH_LONG).show();

                    // Refresh ausschalten
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }

                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);

                // Anzahl der Stunden überprüfen
                if (lessons.size() == 0) {
                    Snackbar.make(mLayout, R.string.room_timetable_add_no_Lessons, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // Verbindung zur Datenbank
                DatabaseManager databaseManager = new DatabaseManager(getActivity());
                TimetableRoomDAO timetableRoomDAO = new TimetableRoomDAO(databaseManager);

                // Daten speichern
                boolean result = timetableRoomDAO.replaceTimetable(room.toUpperCase(), lessons);
                if (result) {
                    EventBus.getInstance().post(new UpdateTimetableEvent());
                    Snackbar.make(mLayout, R.string.room_timetable_add_success, Snackbar.LENGTH_SHORT).show();
                } else
                    Snackbar.make(mLayout, R.string.room_timetable_add_save_error, Snackbar.LENGTH_LONG).show();

                // Lade neue Daten aus der Datenbank
                loadData();
            }
        };

        // Starte Refreshing
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        // Überprüfe Internetverbindung
        if (!VolleyDownloader.CheckInternet(getActivity())) {
            // Refresh ausschalten
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            // Meldung anzeigen
            Snackbar.make(mLayout, R.string.info_no_internet, Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Download der Informationen
        try {
            JsonArrayRequest stringRequest = new JsonArrayRequest("https://www2.htw-dresden.de/~app/API/GetTimetable.php?Room=" + URLEncoder.encode(room, "utf-8"), jsonArrayListener, errorListener);
            VolleyDownloader.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.info_error, Toast.LENGTH_SHORT).show();
        }
    }
}
