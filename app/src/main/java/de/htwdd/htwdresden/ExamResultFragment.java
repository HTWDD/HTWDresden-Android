package de.htwdd.htwdresden;


import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import de.htwdd.htwdresden.adapter.ExamResultListAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.classes.ExamsResultHelper;
import de.htwdd.htwdresden.classes.VolleyDownloader;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.ExamResultDAO;
import de.htwdd.htwdresden.events.UpdateExamResultsEvent;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.ExamResult;


/**
 * Fragment zur Anzeige der Prüfungsergebnisse
 *
 * @author Kay Förster
 */
public class ExamResultFragment extends Fragment {
    private final static String LOG_TAG = "ExamResultFragment";
    private HashMap<Integer, ArrayList<ExamResult>> listExamResults = new HashMap<>();
    private ExamResultListAdapter adapter;
    private View mLayout;

    public ExamResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_exams_result, container, false);
        adapter = new ExamResultListAdapter(getActivity(), listExamResults);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        final ExpandableListView expandableListView = (ExpandableListView) mLayout.findViewById(R.id.expandableListView);
        expandableListView.setAdapter(adapter);

        showData();

        return mLayout;
    }

    /**
     * Behandelt die Benachrichtigung vom Eventbus das neue Prüfungsergebnisse zur Verfügung stehen
     *
     * @param updateExamResultsEvent Typ der Benachrichtigung
     */
    @Subscribe
    public void updateExamResults(UpdateExamResultsEvent updateExamResultsEvent){
        showData();
    }

    /**
     * Zeigt bzw. Aktualisiert die Daten im View
     */
    private void showData() {
        final TextView message = (TextView) mLayout.findViewById(R.id.info_message);

        // Lade Daten aus Datenbank
        final ExamResultDAO dao = new ExamResultDAO(new DatabaseManager(getActivity()));
        final ArrayList<ExamResult> examResults = dao.getAll();

        // Daten in HashMap umwandeln
        listExamResults.clear();
        for (ExamResult examResult : examResults) {
            if (!listExamResults.containsKey(examResult.semester))
                listExamResults.put(examResult.semester, new ArrayList<ExamResult>());
            listExamResults.get(examResult.semester).add(examResult);
        }

        adapter.notifyDataSetChanged();

        // Meldung anzeigen
        if (examResults.size() == 0) {
            message.setText(R.string.exams_result_no_results);
            mLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_background));
        } else {
            message.setText(null);
            mLayout.setBackgroundColor(Color.WHITE);
        }
    }

    private void loadData() {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        final TextView info_message = (TextView) mLayout.findViewById(R.id.info_message);
        final ExamsResultHelper examsResultHelper = new ExamsResultHelper(getActivity());
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Bestimme Fehlermeldung
                int responseCode = VolleyDownloader.getResponseCode(error);

                // Anzahl laufender Requests reduzieren
                examsResultHelper.getQueueCount().decrementCountQueue();

                // Downloads abbrechen
                VolleyDownloader.getInstance(getActivity()).getRequestQueue().cancelAll(Const.internet.TAG_EXAM_RESULTS);

                // Fehlermeldung anzeigen
                final String message;
                switch (responseCode) {
                    case Const.internet.HTTP_TIMEOUT:
                        message = getString(R.string.info_internet_timeout);
                        break;
                    case Const.internet.HTTP_NO_CONNECTION:
                    case Const.internet.HTTP_NOT_FOUND:
                        message = getString(R.string.info_internet_no_connection);
                        break;
                    case Const.internet.HTTP_UNAUTHORIZED:
                        message = getString(R.string.exams_result_wrong_auth);
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

                info_message.setText(message);

                // Refresh ausschalten
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        };
        final Response.Listener<JSONArray> getGradesListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    examsResultHelper.getGradesListener(response);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "[Fehler] beim Parsen: Daten: " + response);
                    Log.e(LOG_TAG, e.toString());

                    // Downloads abbrechen
                    VolleyDownloader.getInstance(getActivity()).getRequestQueue().cancelAll(Const.internet.TAG_EXAM_RESULTS);

                    // Refresh ausschalten
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                    // Fehlermeldung anzeigen
                    Toast.makeText(getActivity(), R.string.info_error_parse, Toast.LENGTH_LONG).show();
                    info_message.setText(R.string.info_error);
                    return;
                }

                // Ergebnisse speichern
                if (examsResultHelper.getQueueCount().getCountQueue() == 0) {
                    final ExamResultDAO dao = new ExamResultDAO(new DatabaseManager(getActivity()));
                    final long count = dao.queryNumEntries();
                    final boolean result = examsResultHelper.saveExamResults();

                    // Refresh ausschalten
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                    // Ergebniss des speicherns überprüfen
                    if (result) {
                        if (count != examsResultHelper.getExamResults().size())
                            Toast.makeText(getActivity(), R.string.exams_result_update_newGrades, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), R.string.exams_result_update_noNewGrades, Toast.LENGTH_SHORT).show();

                        EventBus.getInstance().post(new UpdateExamResultsEvent());
                    } else {
                        Toast.makeText(getActivity(), R.string.info_error_save, Toast.LENGTH_LONG).show();
                        info_message.setText(R.string.info_error_save);
                    }
                }
            }
        };

        /**
         * Response Listener für die Studiengänge
         */
        final Response.Listener<JSONArray> getcoursesListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                // Download der Informationen
                try {
                    examsResultHelper.makeGradeRequests(response, getGradesListener, errorListener);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "[Fehler] beim Parsen: Daten: " + response);
                    Log.e(LOG_TAG, e.toString());

                    // Downloads abbrechen
                    VolleyDownloader.getInstance(getActivity()).getRequestQueue().cancelAll(Const.internet.TAG_EXAM_RESULTS);

                    // Refresh ausschalten
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                    // Fehlermeldung anzeigen
                    Toast.makeText(getActivity(), R.string.info_error_parse, Toast.LENGTH_LONG).show();
                    info_message.setText(R.string.info_error);
                }
            }
        };


        // Überprüfe ob Daten zur Abfrage vorhanden sind.
        if (!examsResultHelper.checkPreferences()) {
            // Zeige Hinweis ein
            info_message.setText(R.string.exams_result_no_settings);

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
            info_message.setText(R.string.info_no_internet);
            Snackbar.make(mLayout, R.string.info_no_internet, Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Download der Informationen
        examsResultHelper.makeCoursesRequest(getcoursesListener, errorListener);
    }
}
