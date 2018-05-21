package de.htwdd.htwdresden;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.htwdd.htwdresden.adapter.ExamListAdapter;
import de.htwdd.htwdresden.classes.API.IExamService;
import de.htwdd.htwdresden.classes.API.Retrofit2Rubu;
import de.htwdd.htwdresden.classes.ConnectionHelper;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.StudyGroupHelper;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.exams.ExamDate;
import de.htwdd.htwdresden.types.studyGroups.StudyGroup;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment zur Anzeige vorhandener Prüfungen
 */
public class ExamsListFragment extends Fragment implements IRefreshing {
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
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        ((SwipeRefreshLayout)mLayout.findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(this::loadData);

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
        final View.OnClickListener clickListenerAdd = view -> {
            stgJhr++;
            changeButtonName(buttonFooterAdd, buttonFooterSub);
            loadData();
        };
        final View.OnClickListener clickListenerDec = view -> {
            stgJhr--;
            changeButtonName(buttonFooterAdd, buttonFooterSub);
            loadData();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
        final Context context = mLayout.getContext();
        final TextView info = mLayout.findViewById(R.id.message_info);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final IExamService iExamService = Retrofit2Rubu.getInstance(context).getRetrofit().create(IExamService.class);
        Call<List<ExamDate>> exams = null;

        // Überprüfe Internetverbindung
        if (ConnectionHelper.checkNoInternetConnection(context)) {
            // Refresh ausschalten
            onCompletion();

            // Meldung anzeigen
            info.setText(R.string.info_no_internet);
            Snackbar.make(mLayout, R.string.info_no_internet, Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Überprüfe Einstellungen
        if (sharedPreferences.getString("ProfName", "").length() > 0) {
            exams = iExamService.getExamSchedule(sharedPreferences.getString("ProfName", ""));
        } else if (TimetableHelper.checkPreferencesSettings(sharedPreferences)) {
            final StudyGroup studyGroup = realm
                    .where(StudyGroup.class)
                    .equalTo(Const.database.StudyGroups.STUDY_GROUP, sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGRUPPE, ""))
                    .equalTo(Const.database.StudyGroups.STUDY_GROUP_COURSE, sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGANG, ""))
                    .equalTo(Const.database.StudyGroups.STUDY_GROUP_COURSE_YEAR, sharedPreferences.getInt(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR, 18))
                    .findFirst();

            if (studyGroup != null && studyGroup.getStudyCourses().first() != null) {
                exams = iExamService.getExamSchedule(
                        stgJhr,
                        sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGANG, ""),
                        StudyGroupHelper.getGraduationChar(studyGroup),
                        sharedPreferences.getString("StgRi", "")
                );
            }
        }

        if (exams == null) {
            info.setText(R.string.exams_no_settings);
            onCompletion();

            Snackbar.make(mLayout, R.string.info_no_settings, Snackbar.LENGTH_LONG)
                    .setAction(R.string.navi_settings, view -> ((INavigation) requireActivity()).goToNavigationItem(R.id.navigation_settings))
                    .show();
            return;
        }

        // View anpassen
        final Button localButton1 = mLayout.findViewById(R.id.Button1);
        final Button localButton2 = mLayout.findViewById(R.id.Button2);

        footer.setVisibility(View.GONE);
        examDates.clear();
        adapter.notifyDataSetChanged();
        localButton1.setVisibility(View.GONE);
        localButton2.setVisibility(View.GONE);
        ((SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout)).setRefreshing(true);

        // API Anfrage ausführen
        exams.enqueue(new Callback<List<ExamDate>>() {
            @Override
            public void onResponse(@NonNull final Call<List<ExamDate>> call, @NonNull final Response<List<ExamDate>> response) {
                // Wenn Response zu langsam und Fragment nicht mehr angezeigt wird, gleich beenden
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful()) {
                    final List<ExamDate> list = response.body();

                    // Meldung falls keine Prüfungen gefunden wurden
                    if (list == null || list.isEmpty()) {
                        // Zusätzliche Buttons zum Wechseln des Jahrganges anzeigen
                        changeButtonName(localButton2, localButton1);
                        // Meldung anzeigen
                        info.setText(R.string.exams_no_exams);
                        return;
                    } else {
                        info.setText(null);
                        footer.setVisibility(View.VISIBLE);
                    }

                    examDates.addAll(list);
                    // Adapter über neue Liste informieren
                    adapter.notifyDataSetChanged();
                } else {
                    info.setText(R.string.info_internet_no_connection);
                    Snackbar.make(mLayout, R.string.info_internet_no_connection, Snackbar.LENGTH_LONG).setAction(R.string.general_repeat, view -> loadData()).show();
                }

                onCompletion();
            }

            @Override
            public void onFailure(@NonNull final Call<List<ExamDate>> call, @NonNull final Throwable t) {
                Log.e(LOG_TAG, "Fehler beim Ausführen des Requests ", t);
                // Wenn Response zu langsam und Fragment nicht mehr angezeigt wird, gleich beenden
                if (isAdded()) {
                    info.setText(R.string.info_internet_error);
                    onCompletion();
                    Snackbar.make(mLayout, R.string.info_internet_error, Snackbar.LENGTH_LONG).setAction(R.string.general_repeat, view -> loadData()).show();
                }
            }
        });
    }

    @Override
    public void onCompletion() {
        ((SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
    }
}
