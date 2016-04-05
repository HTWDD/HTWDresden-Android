package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.classes.LessonHelper;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.classes.VolleyDownloader;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.ExamResultDAO;
import de.htwdd.htwdresden.database.SemesterPlanDAO;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.events.UpdateExamResultsEvent;
import de.htwdd.htwdresden.events.UpdateTimetableEvent;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.ExamStats;
import de.htwdd.htwdresden.types.Lesson;
import de.htwdd.htwdresden.types.LessonSearchResult;
import de.htwdd.htwdresden.types.Meal;
import de.htwdd.htwdresden.types.SemesterPlan;


/**
 * Fragment für den Schnelleinstieg in die App
 */
public class OverviewFragment extends Fragment {
    private final static String LOG_TAG = "OverviewFragment";
    private View mLayout;

    public OverviewFragment() {
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
        mLayout = inflater.inflate(R.layout.fragment_overview, container, false);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final Context context = getActivity();

        // Setze Toolbartitle
        ((INavigation) getActivity()).setTitle(getResources().getString(R.string.navi_overview));

        // Update verfügbar
        CardView cardUpdate = (CardView) mLayout.findViewById(R.id.overview_app_update);
        cardUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www2.htw-dresden.de/~app/android/HTWDresden-latest.apk"));
                startActivity(browserIntent);
            }
        });
        if (sharedPreferences.getBoolean("appUpdate", false))
            cardUpdate.setVisibility(View.VISIBLE);

        // Stundenplan
        CardView cardTimetable = (CardView) mLayout.findViewById(R.id.overview_timetable);
        cardTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((INavigation) getActivity()).goToNavigationItem(R.id.navigation_timetable);
            }
        });

        // Mens
        CardView cardMensa = (CardView) mLayout.findViewById(R.id.overview_mensa);
        cardMensa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((INavigation) getActivity()).goToNavigationItem(R.id.navigation_mensa);
            }
        });

        // Noten
        CardView cardExam = (CardView) mLayout.findViewById(R.id.overview_examResultStats);
        cardExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((INavigation) getActivity()).goToNavigationItem(R.id.navigation_exams);
            }
        });

        // Stundenplan anzeigen
        showTimetable();

        // Noten anzeigen
        showExamResults();

        // Mensa laden
        showMensa();

        showNews();

        // Auf Update überprüfen
        if ((GregorianCalendar.getInstance().getTimeInMillis() - sharedPreferences.getLong("appUpdateCheck", 0) >= TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS)
                && VolleyDownloader.CheckInternet(getActivity()))) {
            // Request mit Listener zur Abfrage der aktuellen Versionen erstellen
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://htwdd.github.io/version.json", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

                        // Überprüfe APK-Version
                        if (response.getInt("androidAPK") > packageInfo.versionCode) {
                            editor.putBoolean("appUpdate", true);
                            // Update-Kachel anzeigen
                            CardView cardUpdate = (CardView) mLayout.findViewById(R.id.overview_app_update);
                            cardUpdate.setVisibility(View.VISIBLE);
                        } else editor.putBoolean("appUpdate", false);

                        // Überprüfe Semesterplan
                        if (response.optLong("semesterplan_update", 0) > sharedPreferences.getLong(Const.preferencesKey.PREFERENCES_SEMESTERPLAN_UPDATETIME, -1))
                            sendRequestToGetSemesterplan();

                        editor.putLong("appUpdateCheck", GregorianCalendar.getInstance().getTimeInMillis());
                        editor.apply();
                    } catch (PackageManager.NameNotFoundException | JSONException e) {
                        Log.e(LOG_TAG, "[Fehler] beim Überprüfen der App-Version: Daten: " + response);
                        Log.e(LOG_TAG, e.toString());
                    }
                }
            }, null);
            VolleyDownloader.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        }

        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        showTimetable();
    }

    /**
     * Behandelt die Benachrichtigung vom Eventbus das neue Prüfungsergebnisse zur Verfügung stehen
     *
     * @param updateExamResultsEvent Typ der Benachrichtigung
     */
    @Subscribe
    public void updateExamResults(UpdateExamResultsEvent updateExamResultsEvent) {
        showExamResults();
    }

    /**
     * Behandelt die Benachrichtigung vom Eventbus das ein neuer Stundenplan zur Verfügung steht
     *
     * @param updateTimetableEvent Typ der Benachrichtigung
     */
    @Subscribe
    public void updateTimetable(UpdateTimetableEvent updateTimetableEvent) {
        showTimetable();
    }

    private void showTimetable() {
        final Context context = getActivity();
        final Calendar calendar = GregorianCalendar.getInstance();
        TimetableUserDAO timetableUserDAO = new TimetableUserDAO(new DatabaseManager(getActivity()));
        final String[] lessonType = mLayout.getResources().getStringArray(R.array.lesson_type);

        // TextViews bestimmen
        final TextView overview_lessons_current_remaining = (TextView) mLayout.findViewById(R.id.overview_lessons_current_remaining);
        final TextView overview_lessons_current_tag = (TextView) mLayout.findViewById(R.id.overview_lessons_current_tag);
        final TextView overview_lessons_current_type = (TextView) mLayout.findViewById(R.id.overview_lessons_current_type);
        final TextView overview_lessons_next_remaining = (TextView) mLayout.findViewById(R.id.overview_lessons_next_remaining);
        final TextView overview_lessons_next_tag = (TextView) mLayout.findViewById(R.id.overview_lessons_next_tag);
        final TextView overview_lessons_next_type = (TextView) mLayout.findViewById(R.id.overview_lessons_next_type);
        final TableRow overview_lessons_busy_plan = (TableRow) mLayout.findViewById(R.id.overview_lessons_busy_plan);
        final LinearLayout overview_lessons_list = (LinearLayout) mLayout.findViewById(R.id.overview_lessons_list);
        final TextView overview_lessons_day = (TextView) mLayout.findViewById(R.id.overview_lesson_day);

        // Aktuelle Stunde anzeigen
        LessonSearchResult lessonSearchResult = LessonHelper.getCurrentUserLesson(context);
        Lesson lesson;
        switch (lessonSearchResult.getCode()) {
            case Const.Timetable.NO_LESSON_FOUND:
                overview_lessons_current_tag.setVisibility(View.GONE);
                overview_lessons_current_type.setText(R.string.overview_lessons_noLesson);
                overview_lessons_current_remaining.setVisibility(View.GONE);
                break;
            case Const.Timetable.ONE_LESSON_FOUND:
                lesson = lessonSearchResult.getLesson();
                assert lesson != null;

                overview_lessons_current_tag.setVisibility(View.VISIBLE);
                overview_lessons_current_tag.setText(lesson.getTag());
                overview_lessons_current_remaining.setVisibility(View.VISIBLE);
                overview_lessons_current_remaining.setText(lessonSearchResult.getTimeRemaining());
                if (!lesson.getRooms().isEmpty())
                    overview_lessons_current_type.setText(
                            mLayout.getResources().getString(
                                    R.string.timetable_ds_list_simple,
                                    lessonType[lesson.getTypeInt()],
                                    lesson.getRooms()));
                else overview_lessons_current_type.setText(lessonType[lesson.getTypeInt()]);
                break;
            case Const.Timetable.MORE_LESSON_FOUND:
                overview_lessons_current_type.setText(null);
                overview_lessons_current_tag.setVisibility(View.VISIBLE);
                overview_lessons_current_tag.setText(R.string.timetable_moreLessons);
                overview_lessons_current_remaining.setVisibility(View.VISIBLE);
                overview_lessons_current_remaining.setText(lessonSearchResult.getTimeRemaining());
                break;
        }

        // Nächste Stunde anzeigen lassen
        lessonSearchResult = LessonHelper.getNextUserLesson(context);
        switch (lessonSearchResult.getCode()) {
            case Const.Timetable.NO_LESSON_FOUND:
                overview_lessons_next_remaining.setText(null);
                overview_lessons_next_tag.setText(null);
                overview_lessons_next_type.setText(null);
                break;
            case Const.Timetable.ONE_LESSON_FOUND:
                lesson = lessonSearchResult.getLesson();
                assert lesson != null;

                overview_lessons_next_remaining.setText(lessonSearchResult.getTimeRemaining());
                overview_lessons_next_tag.setText(lesson.getTag());
                if (!lesson.getRooms().isEmpty()) {
                    overview_lessons_next_type.setText(
                            mLayout.getResources().getString(
                                    R.string.timetable_ds_list_simple,
                                    lessonType[lesson.getTypeInt()],
                                    lesson.getRooms()));
                } else overview_lessons_next_type.setText(lessonType[lesson.getTypeInt()]);
                break;
            case Const.Timetable.MORE_LESSON_FOUND:
                overview_lessons_next_remaining.setText(lessonSearchResult.getTimeRemaining());
                overview_lessons_next_tag.setText(R.string.timetable_moreLessons);
                overview_lessons_next_type.setText(null);
                break;
        }

        // Stundenplanvorschau
        overview_lessons_busy_plan.setVisibility(View.GONE);
        Calendar calendarNextLesson = lessonSearchResult.getCalendar();
        if (lessonSearchResult.getCode() != Const.Timetable.NO_LESSON_FOUND && calendarNextLesson != null) {
            int timeDifference = Math.abs(calendarNextLesson.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR));
            int currentDS = 0;

            switch (timeDifference) {
                case 0:
                    // Bezeichnung ändern
                    overview_lessons_day.setText(R.string.timetable_overview_today);
                    // Wenn Plan von heute, aktuelle Stunde hervorheben
                    currentDS = Const.Timetable.getCurrentDS(null);
                    break;
                case 1:
                    // Bezeichnung ändern
                    overview_lessons_day.setText(R.string.overview_lessons_tomorrow);
                    break;
                default:
                    return;
            }

            // Übersicht anzeigen
            overview_lessons_busy_plan.setVisibility(View.VISIBLE);

            // Daten für Stundenplan-Vorschau
            String[] values = new String[7];
            for (int i = 1; i < 8; i++) {
                ArrayList<Lesson> lessons = timetableUserDAO.getByDS(calendarNextLesson.get(Calendar.WEEK_OF_YEAR), calendarNextLesson.get(Calendar.DAY_OF_WEEK) - 1, i);

                // Suche nach passender Stunde
                LessonSearchResult lessonSearchResult_vorschau = LessonHelper.searchLesson(lessons, calendar.get(Calendar.WEEK_OF_YEAR));

                switch (lessonSearchResult_vorschau.getCode()) {
                    case Const.Timetable.NO_LESSON_FOUND:
                        values[i - 1] = "";
                        break;
                    case Const.Timetable.ONE_LESSON_FOUND:
                        Lesson lesson_vorschau = lessonSearchResult_vorschau.getLesson();
                        assert lesson_vorschau != null;
                        values[i - 1] = lesson_vorschau.getTag() + " (" + lesson_vorschau.getType() + ")";
                        break;
                    case Const.Timetable.MORE_LESSON_FOUND:
                        values[i - 1] = getResources().getString(R.string.timetable_moreLessons);
                        break;
                }
            }

            // Stundenplanvorschau erstellen
            LessonHelper.createSimpleDayOverviewLayout(getActivity(), overview_lessons_list, null, values, currentDS);
        }
    }

    private void showExamResults() {
        final Context context = getActivity();
        final ExamResultDAO dao = new ExamResultDAO(new DatabaseManager(context));
        final ArrayList<ExamStats> examStatses = dao.getStats();
        final ExamStats examStats;
        TextView message = (TextView) mLayout.findViewById(R.id.overview_examResultStatsMessage);
        LinearLayout content = (LinearLayout) mLayout.findViewById(R.id.overview_examResultStatsContent);

        if (examStatses.size() == 0) {
            message.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        } else {
            // Nachricht ausblenden
            message.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);

            examStats = examStatses.get(0);

            // Views
            TextView stats_average = (TextView) mLayout.findViewById(R.id.stats_average);
            TextView stats_countGrade = (TextView) mLayout.findViewById(R.id.stats_countGrade);
            TextView stats_countCredits = (TextView) mLayout.findViewById(R.id.stats_countCredits);
            TextView stats_gradeBest = (TextView) mLayout.findViewById(R.id.stats_gradeBest);
            TextView stats_gradeWorst = (TextView) mLayout.findViewById(R.id.stats_gradeWorst);

            stats_average.setText(context.getString(R.string.exams_stats_average, String.format("%.2f", examStats.average)));
            stats_countGrade.setText(context.getResources().getQuantityString(R.plurals.exams_stats_count_grade, examStats.gradeCount, examStats.gradeCount));
            stats_countCredits.setText(context.getString(R.string.exams_stats_count_credits, examStats.credits));
            stats_gradeBest.setText(context.getString(R.string.exams_stats_gradeBest, examStats.gradeBest));
            stats_gradeWorst.setText(context.getString(R.string.exams_stats_gradeWorst, examStats.gradeWorst));
        }
    }

    private void showMensa() {
        final TextView message = (TextView) mLayout.findViewById(R.id.overview_mensaMessage);
        final TextView content = (TextView) mLayout.findViewById(R.id.overview_mensaContent);
        final Context context = getActivity();
        final short mensaID = 9;

        message.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);

        if (!VolleyDownloader.CheckInternet(context)) {
            message.setText(R.string.info_no_internet);
        }

        message.setText(R.string.overview_mensa_load);

        StringRequest stringRequest = new StringRequest(
                "https://www.studentenwerk-dresden.de/feeds/speiseplan.rss?mid=" + mensaID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MensaHelper mensaHelper = new MensaHelper(context, mensaID);
                        ArrayList<Meal> meals = mensaHelper.parseCurrentDay(response);

                        int count = meals.size();
                        if (count == 0) {
                            message.setText(R.string.mensa_no_offer);
                            message.setVisibility(View.VISIBLE);
                            content.setVisibility(View.GONE);
                            return;
                        }

                        // Anzeige zusammenbauen
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < count - 2; i++) {
                            stringBuilder.append(meals.get(i).getTitle());
                            stringBuilder.append("\n\n");
                        }
                        stringBuilder.append(meals.get(count - 1).getTitle());

                        // Inhalt anzeigen
                        content.setText(stringBuilder);
                        content.setVisibility(View.VISIBLE);
                        message.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        message.setText(R.string.overview_mensa_error);
                        message.setVisibility(View.VISIBLE);
                        content.setVisibility(View.GONE);
                    }
                }
        );
        VolleyDownloader.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void showNews() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("https://www2.htw-dresden.de/~app/API/GetNews.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                final CardView cardView = (CardView) mLayout.findViewById(R.id.overview_news);
                final TextView title = (TextView) mLayout.findViewById(R.id.overview_news_title);
                final WebView content = (WebView) mLayout.findViewById(R.id.overview_news_content);
                final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
                final Calendar tmpCalendar = GregorianCalendar.getInstance();
                final Calendar calendar = GregorianCalendar.getInstance();
                int count = response.length();

                for (int i = 0; i < count; i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // News für Android-Plattform?
                        if (jsonObject.optInt("plattform", 0) != 0)
                            continue;

                        // News noch aktuell?
                        if (!jsonObject.isNull("endDay") && !jsonObject.getString("endDay").isEmpty()) {
                            tmpCalendar.setTime(format.parse(jsonObject.getString("endDay")));
                            if (calendar.after(tmpCalendar))
                                continue;
                        }

                        // Darf News schon angezeigt werden?
                        if (!jsonObject.isNull("beginDay") && !jsonObject.getString("beginDay").isEmpty()) {
                            tmpCalendar.setTime(format.parse(jsonObject.getString("beginDay")));
                            if (tmpCalendar.after(calendar))
                                continue;
                        }

                        // Jetzt darf News angezeigt werden
                        title.setText(jsonObject.getString("title"));
                        content.loadDataWithBaseURL("", jsonObject.getString("content"), "text/html", "UTF-8", "");

                        cardView.setVisibility(View.VISIBLE);
                        if (!jsonObject.isNull("url") && !jsonObject.getString("url").isEmpty()) {
                            final Uri url = Uri.parse(jsonObject.getString("url"));
                            cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
                                    startActivity(browserIntent);
                                }
                            });
                        }

                        // Es kann nur ein News gleichzeitig angeteigt werden, weitere werden nicht betrachtet
                        break;

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null);

        VolleyDownloader.getInstance(getActivity()).getRequestQueue().add(jsonArrayRequest);
    }

    /**
     * Lädt den Semesterplan herunter
     */
    private void sendRequestToGetSemesterplan() {
        final Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    // Datenbankzugriff
                    final SemesterPlanDAO semesterPlanDAO = new SemesterPlanDAO(new DatabaseManager(getActivity()));
                    // Alle Einträge löschen
                    semesterPlanDAO.clearDatabase();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject semesterPlanJSON = response.getJSONObject(i);
                        SemesterPlan semesterPlan = new SemesterPlan(semesterPlanJSON);
                        // Semesterplan speichern
                        semesterPlanDAO.save(semesterPlan);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "[Fehler beim Parsen des Semesterplans]");
                    Log.e(LOG_TAG, e.getMessage());
                    return;
                }

                final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putLong(Const.preferencesKey.PREFERENCES_SEMESTERPLAN_UPDATETIME, System.currentTimeMillis());
                editor.apply();
            }
        };

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Const.internet.WEBSERVICE_URL_SEMESTERPLAN, jsonArrayListener, null);
        VolleyDownloader.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }
}
