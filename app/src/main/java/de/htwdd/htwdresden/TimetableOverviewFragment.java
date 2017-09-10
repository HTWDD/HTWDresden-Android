package de.htwdd.htwdresden;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import de.htwdd.htwdresden.adapter.TimetableUserGridAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.service.TimetableStudentSyncService;
import de.htwdd.htwdresden.types.LessonUser;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class TimetableOverviewFragment extends Fragment {
    private Bundle arguments;
    private Realm realm;
    private RealmResults<LessonUser> lessons;
    private RealmChangeListener<RealmResults<LessonUser>> realmChangeListener;
    private View mLayout;
    private ResponseReceiver responseReceiver;

    public TimetableOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_timetable_overview, container, false);
        realm = Realm.getDefaultInstance();
        arguments = new Bundle(getArguments());

        // SwipeRefreshLayout Listener
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Context context = getActivity();
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                // Überprüfe Einstellungen, ansonsten
                if (sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR, "").length() < 2
                        || sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGANG, "").length() != 3
                        || sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGRUPPE, "").length() == 0) {
                    // Zeige Toast mit Link zu Einstellungen an
                    Snackbar.make(mLayout, R.string.info_no_settings, Snackbar.LENGTH_LONG)
                            .setAction(R.string.navi_settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Navigation ändern
                                    ((INavigation) getActivity()).setNavigationItem(R.id.navigation_settings);
                                    // Fragment "Einstellungen" anzeigen
                                    final FragmentManager fragmentManager = getActivity().getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.activity_main_FrameLayout, new SettingsFragment()).addToBackStack("back").commit();
                                }
                            })
                            .show();
                    // Refresh ausschalten
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    return;
                }

                // Service starten
                Log.d("Time", "Starte Service");
                context.startService(new Intent(context, TimetableStudentSyncService.class));
            }
        });

        // Adapter zum Anzeigen der Daten
        final TimetableUserGridAdapter gridAdapter = new TimetableUserGridAdapter(
                realm,
                arguments.getInt(Const.BundleParams.TIMETABLE_WEEK, 1),
                arguments.getBoolean(Const.BundleParams.TIMETABLE_FILTER_CURRENT_WEEK, true),
                arguments.getBoolean(Const.BundleParams.TIMETABLE_FILTER_SHOW_HIDDEN, false)
        );

        // Benachrichtigung über geänderte Daten
        realmChangeListener = new RealmChangeListener<RealmResults<LessonUser>>() {
            @Override
            public void onChange(final RealmResults<LessonUser> element) {
                gridAdapter.notifyDataSetChanged();
            }
        };
        lessons = realm.where(LessonUser.class).findAll();
        lessons.addChangeListener(realmChangeListener);

        // GridView
        final GridView gridView = (GridView) mLayout.findViewById(R.id.timetable);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                startEditActivity(i, true);
                return true;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startEditActivity(i, false);
            }
        });

        // IntentReceiver erstellen
        final IntentFilter intentFilter = new IntentFilter(Const.IntentParams.BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.addCategory(Const.IntentParams.BROADCAST_FINISH_TIMETABLE_UPDATE);
        responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(responseReceiver, intentFilter);

        return mLayout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lessons.removeChangeListener(realmChangeListener);
        realm.close();
    }

    private void startEditActivity(final int indexOfItem, final boolean editMode) {
        final int day = indexOfItem % 7;
        final Bundle bundle = new Bundle();
        bundle.putInt(Const.BundleParams.TIMETABLE_WEEK, arguments.getInt(Const.BundleParams.TIMETABLE_WEEK, 1));
        bundle.putInt(Const.BundleParams.TIMETABLE_DAY, day);
        bundle.putInt(Const.BundleParams.TIMETABLE_DS, (indexOfItem - day) / 7);
        bundle.putBoolean(Const.BundleParams.TIMETABLE_EDIT, editMode);
        bundle.putBoolean(Const.BundleParams.TIMETABLE_FILTER_CURRENT_WEEK, arguments.getBoolean(Const.BundleParams.TIMETABLE_FILTER_CURRENT_WEEK, true));
        bundle.putBoolean(Const.BundleParams.TIMETABLE_FILTER_SHOW_HIDDEN, arguments.getBoolean(Const.BundleParams.TIMETABLE_FILTER_SHOW_HIDDEN, false));

        final Intent intent = new Intent(getActivity(), TimetableEditActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Empfänger für Updates der Noten vom Service
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            final int intentResponse = intent.getIntExtra(Const.IntentParams.BROADCAST_CODE, -1);
            switch (intentResponse) {
                case 0:
                    Toast.makeText(context, R.string.timetable_sync_success, Toast.LENGTH_LONG).show();
                    break;
                case Const.internet.HTTP_NOT_FOUND:
                    Toast.makeText(context, R.string.timetable_sync_notFound, Toast.LENGTH_SHORT).show();
                    break;
                case Const.internet.HTTP_TIMEOUT:
                case Const.internet.HTTP_UNAUTHORIZED:
                case Const.internet.HTTP_NO_CONNECTION:
                    Toast.makeText(context, intent.getStringExtra(Const.IntentParams.BROADCAST_MESSAGE), Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                default:
                    Toast.makeText(context, R.string.timetable_save_error, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
