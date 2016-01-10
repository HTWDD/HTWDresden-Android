package de.htwdd.htwdresden;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.ArrayList;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.VolleyDownloader;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.Lesson;


public class TimetableDetailFragment extends Fragment {
    private View mLayout;

    public TimetableDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_timetable_detail, container, false);

        // SwipeRefreshLayout Listener
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        return mLayout;
    }


    /**
     * Lädt die entsprechenden Pläne aus dem Internet
     */
    private void loadData() {
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
                Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG).show();

                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int count = response.length();
                ArrayList<Lesson> lessons = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    Lesson lesson = new Lesson();
                    try {
                        lesson.parseFromJSON(response.getJSONObject(i));
                        lessons.add(lesson);

                        // Bestimme End DS und ggf neu eintragen
                        switch (lesson.getDs()) {
                            case 1:
                                if (lesson.getEndTime().before(Const.Timetable.beginDS[1]))
                                    break;
                                lesson = lesson.clone();
                                lesson.setDs(2);
                                lessons.add(lesson);
                            case 2:
                                if (lesson.getEndTime().before(Const.Timetable.beginDS[2]))
                                    break;
                                lesson = lesson.clone();
                                lesson.setDs(3);
                                lessons.add(lesson);
                            case 3:
                                if (lesson.getEndTime().before(Const.Timetable.beginDS[3]))
                                    break;
                                lesson = lesson.clone();
                                lesson.setDs(4);
                                lessons.add(lesson);
                            case 4:
                                if (lesson.getEndTime().before(Const.Timetable.beginDS[4]))
                                    break;
                                lesson = lesson.clone();
                                lesson.setDs(5);
                                lessons.add(lesson);
                            case 5:
                                if (lesson.getEndTime().before(Const.Timetable.beginDS[5]))
                                    break;
                                lesson = lesson.clone();
                                lesson.setDs(6);
                                lessons.add(lesson);
                            case 6:
                                if (lesson.getEndTime().before(Const.Timetable.beginDS[6]))
                                    break;
                                lesson = lesson.clone();
                                lesson.setDs(7);
                                lessons.add(lesson);
                        }
                    } catch (Exception e) {
                        Log.e(this.getClass().getSimpleName(), "[Fehler] beim Parsen: Index: " + i);
                        Log.e(this.getClass().getSimpleName(), e.toString());

                        // Fehlermeldung anzeigen
                        Toast.makeText(getActivity(), R.string.info_error_parse, Toast.LENGTH_LONG).show();

                        // Refresh ausschalten
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                }

                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);

                // Verbindung zur Datenbank
                DatabaseManager databaseManager = new DatabaseManager(getActivity());
                TimetableUserDAO timetableUserDAO = new TimetableUserDAO(databaseManager);
                // Daten speichern
                boolean result = timetableUserDAO.replaceTimetable(lessons);
                if (result)
                    Snackbar.make(mLayout, R.string.timetable_updade_success, Snackbar.LENGTH_SHORT).show();
                else Snackbar.make(mLayout, R.string.timetable_save_error, Snackbar.LENGTH_LONG).show();
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String stgJhr = sharedPreferences.getString("StgJhr", "");
        String stg = sharedPreferences.getString("Stg", "");
        String stgGrp = sharedPreferences.getString("StgGrp", "");
        String prof_kennung = sharedPreferences.getString("prof_kennung", "");

        // Überprüfe Einstellunen, ansonsten
        if ((stgJhr.length() < 2 || stg.length() != 3 || stgGrp.length() == 0) && (prof_kennung.length() == 0)) {
            Snackbar.make(mLayout, R.string.info_no_settings, Snackbar.LENGTH_LONG)
                    .setAction(R.string.navi_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Navigation ändern
                            ((INavigation) getActivity()).setNavigationItem(R.id.navigation_settings);
                            // Einstellungsfragment anzeigen
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.activity_main_FrameLayout, new SettingsFragment()).commit();
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
        int modus;
        if (!(stgJhr.length() < 2 || stg.length() != 3 || stgGrp.length() == 0))
            modus = 1;
        else modus = 2;

        // Wähle URL aus
        String url;
        switch (modus) {
            default:
            case 1:
                url = "https://www2.htw-dresden.de/~app/API/GetTimetable.php?StgJhr=" + stgJhr + "&Stg=" + stg + "&StgGrp=" + stgGrp;
                break;
            case 2:
                url = "https://www2.htw-dresden.de/~app/API/GetTimetable.php?prof_kennung=" + prof_kennung;
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
        JsonArrayRequest stringRequest = new JsonArrayRequest(url, jsonArrayListener, errorListener);
        VolleyDownloader.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
