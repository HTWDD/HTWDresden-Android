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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.htwdd.htwdresden.adapter.ExamListAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.VolleyDownloader;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.Exam;

/**
 * Fragment zur Anzeige vorhandener Prüfungen
 */
public class ExamsListFragment extends Fragment {
    private final static String LOG_TAG = "ExamsListFragment";
    private View mLayout;
    private View footer;
    private int stgJhr;
    private ArrayList<Exam> exams = new ArrayList<>();
    private ExamListAdapter adapter;

    public ExamsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ExamListAdapter(getActivity(), exams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_exams_list, container, false);

        // Studienjahr wiederherstellen, benötigt um zwischen den Jahrgängen wechseln zu können
        if (savedInstanceState != null)
            stgJhr = savedInstanceState.getInt("stgJhr", GregorianCalendar.getInstance().get(Calendar.YEAR) - 2000);
        else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String value = sharedPreferences.getString("StgJhr", "");
            stgJhr = value.isEmpty() ? GregorianCalendar.getInstance().get(Calendar.YEAR) - 2000 : Integer.valueOf(sharedPreferences.getString("StgJhr", ""));
        }

        // Handler für SwipeRefresh
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        // ListView zusammenbauen
        ListView listView = (ListView) mLayout.findViewById(R.id.listView);
        footer = inflater.inflate(R.layout.fragment_exams_footer, listView, false);
        listView.setAdapter(adapter);
        listView.addFooterView(footer);

        // Buttons zum wechseln des Imma-Jahres
        final Button buttonAdd = (Button) mLayout.findViewById(R.id.Button2);
        final Button buttonSub = (Button) mLayout.findViewById(R.id.Button1);
        final Button buttonFooterAdd = (Button) mLayout.findViewById(R.id.examButtonAdd);
        final Button buttonFooterSub = (Button) mLayout.findViewById(R.id.examButtonSub);
        View.OnClickListener clickListenerAdd = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stgJhr++;
                changeButtonName(buttonFooterAdd, buttonFooterSub);
                loadData();
            }
        };
        View.OnClickListener clickListenerDec = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stgJhr--;
                changeButtonName(buttonFooterAdd, buttonFooterSub);
                loadData();
            }
        };

        buttonAdd.setOnClickListener(clickListenerAdd);
        buttonSub.setOnClickListener(clickListenerDec);
        buttonFooterAdd.setOnClickListener(clickListenerAdd);
        buttonFooterSub.setOnClickListener(clickListenerDec);
        changeButtonName(buttonFooterAdd, buttonFooterSub);

        // Lade Daten
        loadData();

        return mLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("stgJhr", stgJhr);
    }

    /**
     * Buttons zum wechseln der Jahrgänge beschriften und ggf. ausblenden
     *
     * @param buttonInc View für den Button zum inkrementieren des Jahrgangs
     * @param buttonDec View für den Button zum dekrementieren des Jahrgangs
     */
    private void changeButtonName(Button buttonInc, Button buttonDec) {
        int currentYear = GregorianCalendar.getInstance().get(Calendar.YEAR) - 2000;
        buttonInc.setText(getString(R.string.exams_from_Jhr, String.valueOf(stgJhr + 1)));
        buttonDec.setText(getString(R.string.exams_from_Jhr, String.valueOf(stgJhr - 1)));

        if (stgJhr >= currentYear)
            buttonInc.setVisibility(View.GONE);
        else buttonInc.setVisibility(View.VISIBLE);
        if (stgJhr <= currentYear - 6)
            buttonDec.setVisibility(View.GONE);
        else buttonDec.setVisibility(View.VISIBLE);
    }

    /**
     * Lädt den Prüfungsplan anhand der Einstellungen und des gesetzten Jahrgangs vom Webservice
     */
    private void loadData() {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        final TextView info = (TextView) mLayout.findViewById(R.id.message_info);
        final Button localButton1 = (Button) mLayout.findViewById(R.id.Button1);
        final Button localButton2 = (Button) mLayout.findViewById(R.id.Button2);
        String url;

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
                        loadData();
                    }
                }).show();

                info.setText(message);

                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);

                // Meldung falls keine Prüfungen gefunden wurden
                int count = response.length();
                if (count == 0) {
                    // Zusätzliche Buttons zum Wechseln des Jahrganges anzeigen
                    changeButtonName(localButton2, localButton1);
                    // Beldung anzeigen
                    info.setText(R.string.exams_no_exams);
                    return;
                } else {
                    info.setText(null);
                    footer.setVisibility(View.VISIBLE);
                }

                // Prüfungen einseln parsen und zur Liste hinzufügen
                for (int i = 0; i < count; i++) {
                    try {
                        Exam exam = new Exam();
                        exam.parseFromJSON(response.getJSONObject(i));
                        exams.add(exam);
                    } catch (Exception e) {
                        // Fehler intern loggen
                        Log.e(LOG_TAG, "[Fehler] beim Parsen: Daten: " + response);
                        Log.e(LOG_TAG, e.toString());

                        // Fehlermeldung anzeigen
                        Toast.makeText(getActivity(), R.string.info_error_parse, Toast.LENGTH_LONG).show();
                        info.setText(R.string.info_error);
                        return;
                    }
                }

                // Adapter über neue Liste informieren
                adapter.notifyDataSetChanged();
            }
        };

        // Refresh anschalten
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        // Liste ausblenden
        footer.setVisibility(View.GONE);
        exams.clear();
        adapter.notifyDataSetChanged();

        // Extra Button ausblenden
        localButton1.setVisibility(View.GONE);
        localButton2.setVisibility(View.GONE);

        // Überprüfe ob Daten zur Abfrage vorhanden sind.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedPreferences.getString("abschluss", "").length() > 0 && sharedPreferences.getString("StgJhr", "").length() > 0 && sharedPreferences.getString("Stg", "").length() > 0) {
            url = "GetExams.php?StgJhr=" + stgJhr
                    + "&Stg=" + sharedPreferences.getString("Stg", "")
                    + "&AbSc=" + sharedPreferences.getString("abschluss", "")
                    + "&Stgri=" + sharedPreferences.getString("StgRi", "");
        } else if (sharedPreferences.getString("ProfName", "").length() > 0) {
            url = "GetExams.phpetExams.php?Prof=" + sharedPreferences.getString("ProfName", "");
        } else {
            // Zeige Hinweis ein
            info.setText(R.string.exams_no_settings);

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
            info.setText(R.string.info_no_internet);
            Snackbar.make(mLayout, R.string.info_no_internet, Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Download der Informationen
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Const.internet.WEBSERVICE_URL + url, jsonArrayListener, errorListener);
        VolleyDownloader.getInstance(getActivity()).addToRequestQueue(arrayRequest);
    }
}
