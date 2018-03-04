package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Context;
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
import de.htwdd.htwdresden.classes.StudyGroupHelper;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.exams.ExamDate;
import de.htwdd.htwdresden.types.studyGroups.StudyGroup;
import io.realm.Realm;

/**
 * Fragment zur Anzeige vorhandener Prüfungen
 */
public class ExamsListFragment extends Fragment {
    private final static String LOG_TAG = "ExamsListFragment";
    private Realm realm;
    private View mLayout;
    private View footer;
    private int stgJhr;
    private final ArrayList<ExamDate> examDates = new ArrayList<>();
    private ExamListAdapter adapter;

    public ExamsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ExamListAdapter(getActivity(), examDates);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_exams_list, container, false);
        realm = Realm.getDefaultInstance();

        // Studienjahr wiederherstellen, benötigt um zwischen den Jahrgängen wechseln zu können
        if (savedInstanceState != null) {
            stgJhr = savedInstanceState.getInt("stgJhr", GregorianCalendar.getInstance().get(Calendar.YEAR) - 2000);
        }
        else {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mLayout.getContext());
            stgJhr = sharedPreferences.getInt(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR, GregorianCalendar.getInstance().get(Calendar.YEAR) - 2000);
        }

        // Handler für SwipeRefresh
        final SwipeRefreshLayout swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        // ListView zusammenbauen
        final ListView listView = mLayout.findViewById(R.id.listView);
        footer = inflater.inflate(R.layout.fragment_exams_footer, listView, false);
        listView.setAdapter(adapter);
        listView.addFooterView(footer);

        // Buttons zum wechseln des Imma-Jahres
        final Button buttonAdd = mLayout.findViewById(R.id.Button2);
        final Button buttonSub = mLayout.findViewById(R.id.Button1);
        final Button buttonFooterAdd = mLayout.findViewById(R.id.examButtonAdd);
        final Button buttonFooterSub = mLayout.findViewById(R.id.examButtonSub);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
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
        final SwipeRefreshLayout swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        final TextView info = mLayout.findViewById(R.id.message_info);
        final Button localButton1 = mLayout.findViewById(R.id.Button1);
        final Button localButton2 = mLayout.findViewById(R.id.Button2);
        String url = null;

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                // Wenn Response zu langsam und Fragment nicht mehr angezeigt wird, gleich beenden
                if (!isAdded()) {
                    return;
                }
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
            public void onResponse(final JSONArray response) {
                // Wenn Response zu langsam und Fragment nicht mehr angezeigt wird, gleich beenden
                if (!isAdded()) {
                    return;
                }

                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);

                // Meldung falls keine Prüfungen gefunden wurden
                int count = response.length();
                if (count == 0) {
                    // Zusätzliche Buttons zum Wechseln des Jahrganges anzeigen
                    changeButtonName(localButton2, localButton1);
                    // Meldung anzeigen
                    info.setText(R.string.exams_no_exams);
                    return;
                } else {
                    info.setText(null);
                    footer.setVisibility(View.VISIBLE);
                }

                // Prüfungen einseln parsen und zur Liste hinzufügen
                for (int i = 0; i < count; i++) {
                    try {
                        ExamDate examDate = new ExamDate();
                        examDate.parseFromJSON(response.getJSONObject(i));
                        examDates.add(examDate);
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
        examDates.clear();
        adapter.notifyDataSetChanged();

        // Extra Button ausblenden
        localButton1.setVisibility(View.GONE);
        localButton2.setVisibility(View.GONE);

        // Überprüfe ob Daten zur Abfrage vorhanden sind.
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mLayout.getContext());
        final String studyCoursePreferences = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGANG, "");

        // Überprüfung für Studenten
        if (sharedPreferences.contains(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR)
                && sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGRUPPE, "").length() > 0
                && studyCoursePreferences.length() > 0) {

            final StudyGroup studyGroup = realm
                    .where(StudyGroup.class)
                    .equalTo(Const.database.StudyGroups.STUDY_GROUP, sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGRUPPE, ""))
                    .equalTo(Const.database.StudyGroups.STUDY_GROUP_COURSE, studyCoursePreferences)
                    .equalTo(Const.database.StudyGroups.STUDY_GROUP_COURSE_YEAR, sharedPreferences.getInt(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR, 18))
                    .findFirst();

            if (studyGroup != null) {
                url = "GetExams.php?StgJhr=" + stgJhr
                        + "&Stg=" + studyGroup.getStudyCourses().first().getStudyCourse()
                        + "&AbSc=" + StudyGroupHelper.getGraduationChar(studyGroup)
                        + "&Stgri=" + sharedPreferences.getString("StgRi", "");
            }
        }
        // Überprüfung für Professoren
        else if (sharedPreferences.getString("ProfName", "").length() > 0) {
            url = "GetExams.php?Prof=" + sharedPreferences.getString("ProfName", "");
        }

        // Wenn keine Einstellungen gefunden wurden, Fehlermeldung anzeigen
        if (url == null) {
            info.setText(R.string.exams_no_settings);

            Snackbar.make(mLayout, R.string.info_no_settings, Snackbar.LENGTH_LONG)
                    .setAction(R.string.navi_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((INavigation) getActivity()).goToNavigationItem(R.id.navigation_settings);
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
        final Context context = mLayout.getContext();
        if (!VolleyDownloader.CheckInternet(context)) {
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
        final JsonArrayRequest arrayRequest = new JsonArrayRequest(Const.internet.WEBSERVICE_URL + url, jsonArrayListener, errorListener);
        VolleyDownloader.getInstance(context).addToRequestQueue(arrayRequest);
    }
}
