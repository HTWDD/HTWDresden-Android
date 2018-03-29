package de.htwdd.htwdresden;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.htwdd.htwdresden.classes.API.IGeneralService;
import de.htwdd.htwdresden.classes.API.Retrofit2Rubu;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.ExamsHelper;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.classes.NextLessonResult;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.LessonUser;
import de.htwdd.htwdresden.types.News;
import de.htwdd.htwdresden.types.canteen.Meal;
import de.htwdd.htwdresden.types.exams.ExamResult;
import de.htwdd.htwdresden.types.exams.ExamStats;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_overview, container, false);
        final INavigation navigation = (INavigation) getActivity();

        if (navigation != null) {
            // Stundenplan
            mLayout.findViewById(R.id.overview_timetable).setOnClickListener(view -> navigation.goToNavigationItem(R.id.navigation_timetable));

            // Navigation zur Mensa
            mLayout.findViewById(R.id.overview_mensa).setOnClickListener(view -> navigation.goToNavigationItem(R.id.navigation_mensa));

            // Noten
            mLayout.findViewById(R.id.overview_examResultStats).setOnClickListener(view -> navigation.goToNavigationItem(R.id.navigation_exams));
        }

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
        final Context context = mLayout.getContext();
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
        content.setText(MensaHelper.concatTitels(mLayout.getContext(), meals));
        message.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

    private void showNews() {
        final IGeneralService iGeneralService = Retrofit2Rubu.getInstance(mLayout.getContext()).getRetrofit().create(IGeneralService.class);
        final Call<List<News>> news = iGeneralService.getNews();
        news.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(@NonNull final Call<List<News>> call, @NonNull final Response<List<News>> response) {
                final List<News> newsList = response.body();
                final Date calendar = GregorianCalendar.getInstance().getTime();

                if (response.isSuccessful() && newsList != null) {
                    for (final News news : newsList) {
                        // News relevant?
                        if (news.getPlattform() != 0 || (news.getEndDay() != null && calendar.after(news.getEndDay())) || (news.getBeginDay() != null && calendar.before(news.getBeginDay()))) {
                            continue;
                        }

                        final CardView cardView = mLayout.findViewById(R.id.overview_news);
                        cardView.setVisibility(View.VISIBLE);
                        ((TextView) mLayout.findViewById(R.id.overview_news_title)).setText(news.getTitle());
                        ((WebView) mLayout.findViewById(R.id.overview_news_content)).loadDataWithBaseURL("", news.getContent(), "text/html", "UTF-8", "");

                        if (news.getUrl() != null) {
                            cardView.setOnClickListener(view -> {
                                final Uri url = Uri.parse(news.getUrl());
                                final Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
                                startActivity(browserIntent);
                            });
                        }

                        break;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull final Call<List<News>> call, @NonNull final Throwable t) {
                Log.i("OverviewFragment", "Fehler beim Ausführen des Requests ", t);
            }
        });
    }
}
