package de.htwdd.htwdresden;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.adapter.TimetableGridAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.classes.LessonHelper;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.events.UpdateTimetableEvent;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.Lesson;


public class TimetableOverviewFragment extends Fragment {
    private View mLayout;
    private int week;
    private TimetableGridAdapter gridAdapter;
    private ArrayList<Lesson> lessons_week;

    public TimetableOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lessons_week = new ArrayList<>();
        EventBus.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Erst Abmelden wenn Fragment entgültig zerstört wird, da ansonsten Nachrichten aus anderen
        // Activitys nicht registriert werden.
        EventBus.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_timetable_overview, container, false);

        // Arguments überprüfen
        final Bundle bundle = getArguments();
        if (bundle != null)
            week = bundle.getInt(Const.BundleParams.TIMETABLE_WEEK, new GregorianCalendar(Locale.GERMANY).get(Calendar.WEEK_OF_YEAR));
        else week = new GregorianCalendar(Locale.GERMANY).get(Calendar.WEEK_OF_YEAR);

        // SwipeRefreshLayout Listener
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        // Lade Daten aus DB
        loadLessons();

        // Adapter zum handeln der Daten
        gridAdapter = new TimetableGridAdapter(getActivity(), lessons_week, week);

        // GridView
        final GridView gridView = (GridView) mLayout.findViewById(R.id.timetable);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt(Const.BundleParams.TIMETABLE_WEEK, week);
                bundle1.putInt(Const.BundleParams.TIMETABLE_DAY, i % 7);
                bundle1.putInt(Const.BundleParams.TIMETABLE_DS, i / 7);
                bundle1.putBoolean(Const.BundleParams.TIMETABLE_EDIT, true);

                Intent intent = new Intent(getActivity(), TimetableEditActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
                return true;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt(Const.BundleParams.TIMETABLE_WEEK, week);
                bundle1.putInt(Const.BundleParams.TIMETABLE_DAY, i % 7);
                bundle1.putInt(Const.BundleParams.TIMETABLE_DS, i / 7);

                Intent intent = new Intent(getActivity(), TimetableEditActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

        return mLayout;
    }

    /**
     * Behandelt die Benachrichtigung vom Eventbus das ein neuer Stundenplan zur Verfügung steht
     *
     * @param event Typ der Benachrichtigung
     */
    @Subscribe
    public void updateTimetable(UpdateTimetableEvent event) {
        loadLessons();
        gridAdapter.notifyDataSetChanged();
    }

    /**
     * Lädt die Stunden der aktuellen Woche {@see week} aus der Datenbank und speichert sie in einer
     * Liste {@see lesson_week}
     */
    void loadLessons() {
        final DatabaseManager databaseManager = new DatabaseManager(getActivity());
        final TimetableUserDAO timetableUserDAO = new TimetableUserDAO(databaseManager);
        lessons_week.clear();
        lessons_week.addAll(timetableUserDAO.getWeekShort(week));
    }

    /**
     * Lädt die entsprechenden Pläne aus dem Internet
     */
    private void loadData() {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
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
                        loadData();
                    }
                }).show();

                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        final Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
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

                // Verbindung zur Datenbank
                DatabaseManager databaseManager = new DatabaseManager(getActivity());
                TimetableUserDAO timetableUserDAO = new TimetableUserDAO(databaseManager);
                // Daten speichern
                boolean result = timetableUserDAO.replaceTimetable(lessons);
                if (result) {
                    EventBus.getInstance().post(new UpdateTimetableEvent());
                    Snackbar.make(mLayout, R.string.timetable_updade_success, Snackbar.LENGTH_SHORT).show();
                } else
                    Snackbar.make(mLayout, R.string.timetable_save_error, Snackbar.LENGTH_LONG).show();
            }
        };

        // Starte Refreshing
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        // Hole Einstellungen
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String stgJhr = sharedPreferences.getString("StgJhr", "");
        final String stg = sharedPreferences.getString("Stg", "");
        final String stgGrp = sharedPreferences.getString("StgGrp", "");
        final String prof_kennung = sharedPreferences.getString("prof_kennung", "");

        // Überprüfe Einstellunen, ansonsten
        if ((stgJhr.length() < 2 || stg.length() != 3 || stgGrp.length() == 0) && (prof_kennung.length() == 0)) {
            Snackbar.make(mLayout, R.string.info_no_settings, Snackbar.LENGTH_LONG)
                    .setAction(R.string.navi_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Navigation ändern
                            ((INavigation) getActivity()).setNavigationItem(R.id.navigation_settings);
                            // Einstellungsfragment anzeigen
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

        // Auswahl was geladen werden soll
        final int modus;
        if (!(stgJhr.length() < 2 || stg.length() != 3 || stgGrp.length() == 0))
            modus = 1;
        else modus = 2;

        // Wähle URL aus
        final String url;
        switch (modus) {
            default:
            case 1:
                url = "https://rubu2.rz.htw-dresden.de/API/v0/studentTimetable.php?StgJhr=" + stgJhr + "&Stg=" + stg + "&StgGrp=" + stgGrp;
                break;
            case 2:
                url = Const.internet.WEBSERVICE_URL + "GetTimetable.php?Prof=" + prof_kennung;
                break;
        }

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
        final JsonArrayRequest arrayRequest = new JsonArrayRequest(url, jsonArrayListener, errorListener);
        VolleyDownloader.getInstance(getActivity()).addToRequestQueue(arrayRequest);
    }
}
