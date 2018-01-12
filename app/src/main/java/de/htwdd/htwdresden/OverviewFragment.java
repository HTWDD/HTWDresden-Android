package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.ExamsHelper;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.classes.NextLessonResult;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.ExamResult;
import de.htwdd.htwdresden.types.ExamStats;
import de.htwdd.htwdresden.types.LessonUser;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Fragment für den Schnelleinstieg in die App
 */
public class OverviewFragment extends Fragment {
    private View mLayout;
    // Datenbank
    private Realm realm;
    private RealmResults<ExamResult> examResults;
    private RealmResults<Meal> meals;
    private RealmResults<LessonUser> lessons;
    private RealmChangeListener<RealmResults<ExamResult>> realmListenerExams;
    private RealmChangeListener<RealmResults<Meal>> realmListenerMensa;
    private RealmChangeListener<RealmResults<LessonUser>> realmListenerLessons;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_overview, container, false);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Setze Toolbar Titel
        ((INavigation) getActivity()).setTitle(getResources().getString(R.string.navi_overview));

        // Update verfügbar
        final CardView cardUpdate = mLayout.findViewById(R.id.overview_app_update);
        cardUpdate.setOnClickListener(view -> {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www2.htw-dresden.de/~app/android/HTWDresden-latest.apk"));
            startActivity(browserIntent);
        });
        if (sharedPreferences.getBoolean("appUpdate", false)) {
            cardUpdate.setVisibility(View.VISIBLE);
        }

        // Stundenplan
        mLayout.findViewById(R.id.overview_timetable).setOnClickListener(view -> ((INavigation) getActivity()).goToNavigationItem(R.id.navigation_timetable));

        // Navigation zur Mensa
        mLayout.findViewById(R.id.overview_mensa).setOnClickListener(view -> ((INavigation) getActivity()).goToNavigationItem(R.id.navigation_mensa));

        // Noten
        mLayout.findViewById(R.id.overview_examResultStats).setOnClickListener(view -> ((INavigation) getActivity()).goToNavigationItem(R.id.navigation_exams));

        // Daten für Mensa laden und anzeigen
        final Calendar calendar = GregorianCalendar.getInstance();
        realm = Realm.getDefaultInstance();
        realmListenerMensa = element -> showMensaInfo(meals);
        meals = realm.where(Meal.class).equalTo(Const.database.Canteen.MENSA_DATE, MensaHelper.getDate(calendar)).equalTo(Const.database.Canteen.MENSA_ID, 1).findAll();
        meals.addChangeListener(realmListenerMensa);
        showMensaInfo(meals);

        // Übersicht über Noten
        realmListenerExams = element -> showExamStats(element.size() > 0);
        examResults = realm.where(ExamResult.class).findAll();
        examResults.addChangeListener(realmListenerExams);
        showExamStats(realm.where(ExamResult.class).count() > 0);

        // Change Listener für Lehrveranstaltungen
        realmListenerLessons = element -> showUserTimetableOverview();
        lessons = realm.where(LessonUser.class).findAll();
        lessons.addChangeListener(realmListenerLessons);

        // News laden
        showNews();

        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Übersicht aktualisieren
        showUserTimetableOverview();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        examResults.removeChangeListener(realmListenerExams);
        meals.removeChangeListener(realmListenerMensa);
        lessons.removeChangeListener(realmListenerLessons);
        realm.close();
    }

    /**
     * Zeigt eine Gesamtstatistik der Noten an oder einen entsprechenden Hinweis
     *
     * @param examsAvailable Sind Noten vorhanden?
     */
    private void showExamStats(final boolean examsAvailable) {
        final TextView message = mLayout.findViewById(R.id.overview_examResultStatsMessage);
        final LinearLayout content = mLayout.findViewById(R.id.overview_examResultStatsContent);

        if (examsAvailable) {
            message.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);

            // Erstelle Statistik
            final ExamStats examStats = ExamsHelper.getExamStatsForSemester(realm, null);

            // Views holen
            final TextView stats_average = mLayout.findViewById(R.id.stats_average);
            final TextView stats_countGrade = mLayout.findViewById(R.id.stats_countGrade);
            final TextView stats_countCredits = mLayout.findViewById(R.id.stats_countCredits);
            final TextView stats_gradeBest = mLayout.findViewById(R.id.stats_gradeBest);
            final TextView stats_gradeWorst = mLayout.findViewById(R.id.stats_gradeWorst);

            stats_average.setText(getString(R.string.exams_stats_average, String.format(Locale.getDefault(), "%.2f", examStats.getAverage())));
            stats_countGrade.setText(getResources().getQuantityString(R.plurals.exams_stats_count_grade, (int) examStats.gradeCount, (int) examStats.gradeCount));
            stats_countCredits.setText(getString(R.string.exams_stats_count_credits, examStats.getCredits()));
            stats_gradeBest.setText(getString(R.string.exams_stats_gradeBest, examStats.getGradeBest()));
            stats_gradeWorst.setText(getString(R.string.exams_stats_gradeWorst, examStats.getGradeWorst()));
        } else {
            message.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        }
    }

    /**
     * Übersicht über Stundenplan
     */
    private void showUserTimetableOverview() {
        final Context context = getActivity();
        final String[] lessonType = mLayout.getResources().getStringArray(R.array.lesson_type);

        // TextViews bestimmen
        final TextView overview_lessons_current_remaining = mLayout.findViewById(R.id.overview_lessons_current_remaining);
        final TextView overview_lessons_current_tag = mLayout.findViewById(R.id.overview_lessons_current_tag);
        final TextView overview_lessons_current_type = mLayout.findViewById(R.id.overview_lessons_current_type);
        final TextView overview_lessons_next_remaining = mLayout.findViewById(R.id.overview_lessons_next_remaining);
        final TextView overview_lessons_next_tag = mLayout.findViewById(R.id.overview_lessons_next_tag);
        final TextView overview_lessons_next_type = mLayout.findViewById(R.id.overview_lessons_next_type);
        final TableRow overview_lessons_busy_plan = mLayout.findViewById(R.id.overview_lessons_busy_plan);
        final LinearLayout overview_lessons_list = mLayout.findViewById(R.id.overview_lessons_list);
        final TextView overview_lessons_day = mLayout.findViewById(R.id.overview_lesson_day);

        // Aktuelle Stunde anzeigen
        final RealmResults<LessonUser> currentLesson = TimetableHelper.getCurrentLessons(realm);
        LessonUser lesson;

        switch (currentLesson.size()) {
            case 0:
                overview_lessons_current_tag.setVisibility(View.GONE);
                overview_lessons_current_type.setText(R.string.overview_lessons_noLesson);
                overview_lessons_current_remaining.setVisibility(View.GONE);
                break;
            case 1:
                lesson = currentLesson.first();
                overview_lessons_current_tag.setVisibility(View.VISIBLE);
                overview_lessons_current_tag.setText(lesson.getLessonTag());
                overview_lessons_current_remaining.setVisibility(View.VISIBLE);
                overview_lessons_current_remaining.setText(TimetableHelper.getStringRemainingTime(context));
                if (lesson.getRooms().size() > 0) {
                    overview_lessons_current_type.setText(
                            getString(
                                    R.string.timetable_ds_list_simple,
                                    lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)],
                                    TimetableHelper.getStringOfRooms(lesson)
                            )
                    );
                } else overview_lessons_current_type.setText(lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)]);
                break;
            default:
                overview_lessons_current_type.setText(null);
                overview_lessons_current_tag.setVisibility(View.VISIBLE);
                overview_lessons_current_tag.setText(R.string.timetable_moreLessons);
                overview_lessons_current_remaining.setVisibility(View.VISIBLE);
                overview_lessons_current_remaining.setText(TimetableHelper.getStringRemainingTime(context));
                break;
        }

        // Nächste Stunde anzeigen
        final NextLessonResult nextLessonResult = TimetableHelper.getNextLessons(realm);
        if (nextLessonResult.getResults() == null || nextLessonResult.getResults().size() == 0) {
            overview_lessons_next_remaining.setText(null);
            overview_lessons_next_tag.setText(null);
            overview_lessons_next_type.setText(null);
        } else if (nextLessonResult.getResults().size() == 1) {
            lesson = nextLessonResult.getResults().first();
            overview_lessons_next_tag.setText(lesson.getLessonTag());
            overview_lessons_next_remaining.setText(TimetableHelper.getStringStartNextLesson(context, nextLessonResult));
            if (lesson.getRooms().size() > 0) {
                overview_lessons_next_type.setText(
                        getString(
                                R.string.timetable_ds_list_simple,
                                lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)],
                                TimetableHelper.getStringOfRooms(lesson)
                        ));
            } else overview_lessons_next_type.setText(lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)]);
        } else {
            overview_lessons_next_remaining.setText(TimetableHelper.getStringStartNextLesson(context, nextLessonResult));
            overview_lessons_next_tag.setText(R.string.timetable_moreLessons);
            overview_lessons_next_type.setText(null);
        }

        // Vorschau des aktuellen Stundenplans
        overview_lessons_busy_plan.setVisibility(View.GONE);
        if (nextLessonResult.getResults() != null && nextLessonResult.getResults().size() > 0) {
            final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
            final int differenceDay = Math.abs(nextLessonResult.getOnNextDay().get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR));
            int currentDs = 0;

            // Vorschau nur für heute und morgen anzeigen
            if (differenceDay > 1) {
                return;
            } else if (differenceDay == 0) {
                overview_lessons_day.setText(R.string.timetable_overview_today);
                currentDs = TimetableHelper.getCurrentDS(TimetableHelper.getMinutesSinceMidnight(calendar));
            } else {
                overview_lessons_day.setText(R.string.overview_lessons_tomorrow);
            }

            // Übersicht anzeigen
            overview_lessons_busy_plan.setVisibility(View.VISIBLE);

            // Vorschau des Stundenplans erstellen
            overview_lessons_list.removeAllViews();
            TimetableHelper.createSimpleLessonOverview(context, realm, overview_lessons_list, nextLessonResult.getOnNextDay(), currentDs);
        }
    }

    /**
     * Zeigt die Mahlzeiten als einfache Liste an
     *
     * @param meals Liste der Mahlzeiten
     */
    private void showMensaInfo(@NonNull final RealmResults<Meal> meals) {
        final TextView message = mLayout.findViewById(R.id.overview_mensaMessage);
        final TextView content = mLayout.findViewById(R.id.overview_mensaContent);

        // Aktuell kein Angebot vorhanden
        if (meals.size() == 0) {
            message.setText(R.string.mensa_no_offer);
            message.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            return;
        }

        // Inhalt anzeigen
        content.setText(MensaHelper.concatTitels(getActivity(), meals));
        message.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

    private void showNews() {
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Const.internet.WEBSERVICE_URL_NEWS, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                final CardView cardView = mLayout.findViewById(R.id.overview_news);
                final TextView title = mLayout.findViewById(R.id.overview_news_title);
                final WebView content = mLayout.findViewById(R.id.overview_news_content);
                final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
                final Calendar tmpCalendar = GregorianCalendar.getInstance();
                final Calendar calendar = GregorianCalendar.getInstance();
                int count = response.length();

                for (int i = 0; i < count; i++) {
                    try {
                        final JSONObject jsonObject = response.getJSONObject(i);

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

                        // Es kann nur eine News gleichzeitig angezeigt werden, weitere werden nicht betrachtet
                        break;

                    } catch (final JSONException | ParseException e) {
                        Log.e("OverviewFragment", "[Error] beim Verarbeiten der News", e);
                    }
                }
            }
        }, null);

        VolleyDownloader.getInstance(getActivity()).getRequestQueue().add(jsonArrayRequest);
    }
}
