package de.htwdd.htwdresden;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
import de.htwdd.htwdresden.types.semsterPlan.TimePeriod;
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
    private RealmChangeListener<RealmResults<ExamResult>> realmListenerExams;
    private RealmChangeListener<RealmResults<Meal>> realmListenerMensa;
    private final Handler refreshHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshHandler.postDelayed(this, 60 * 1000);
            showUserTimetableOverview();
        }
    };

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
            mLayout.findViewById(R.id.overview_timetable_lessons_day).setOnClickListener(view -> navigation.goToNavigationItem(R.id.navigation_timetable));
            mLayout.findViewById(R.id.overview_timetable_lessons_list).setOnClickListener(view -> navigation.goToNavigationItem(R.id.navigation_timetable));

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

        // News laden
        showNews();

        // Übersicht aktualisieren
        showUserTimetableOverview();

        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        final Calendar calendar = Calendar.getInstance();
        refreshHandler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(60 - calendar.get(Calendar.SECOND)));
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshHandler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        examResults.removeChangeListener(realmListenerExams);
        meals.removeChangeListener(realmListenerMensa);
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
     * Anzeige der aktuellen oder als nächstes startenden Lehrveranstaltung an
     *
     * @param lesson      anzuzeigende Lehrveranstaltung, bei mehreren die ersten
     * @param manyLessons True, wenn mehrere Veranstaltungen zur gleichen Zeit stattfinden, sonst false
     */
    private void showLessonInfoSlot1(@Nullable final LessonUser lesson, final boolean manyLessons) {
        final Context context = mLayout.getContext();
        final TextView overview_lesson_slot1_name = mLayout.findViewById(R.id.overview_lesson_slot1_name);
        final TextView overview_lesson_slot1_room = mLayout.findViewById(R.id.overview_lesson_slot1_room);
        final TextView overview_lesson_slot1_time = mLayout.findViewById(R.id.overview_lesson_slot1_time);
        final TextView overview_lesson_slot1_art = mLayout.findViewById(R.id.overview_lesson_slot1_art);

        if (lesson != null && !manyLessons) {
            final String[] lessonType = mLayout.getResources().getStringArray(R.array.lesson_type);
            overview_lesson_slot1_name.setText(lesson.getName());
            overview_lesson_slot1_room.setText(TimetableHelper.getStringOfRooms(lesson));
            overview_lesson_slot1_time.setText(TimetableHelper.getStringRemainingTime(context, lesson));
            overview_lesson_slot1_art.setText(lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)]);
        } else if (lesson != null) {
            overview_lesson_slot1_time.setText(TimetableHelper.getStringRemainingTime(context, lesson));
            overview_lesson_slot1_name.setText(R.string.timetable_moreLessons);
            overview_lesson_slot1_art.setVisibility(View.GONE);
            overview_lesson_slot1_room.setVisibility(View.GONE);
        } else {
            overview_lesson_slot1_name.setVisibility(View.GONE);
            overview_lesson_slot1_room.setVisibility(View.GONE);
            overview_lesson_slot1_time.setVisibility(View.GONE);
            overview_lesson_slot1_art.setVisibility(View.GONE);
        }
    }

    /**
     * Anzeige der nächsten Veranstaltung
     *
     * @param searchResult Suchergebnis welches nächste Stunde enthält
     */
    private void showLessonInfoSlot2(@NonNull final NextLessonResult searchResult) {
        final Context context = mLayout.getContext();
        final RealmResults<LessonUser> lessons = searchResult.getResults();
        final LessonUser firstLesson = lessons != null && !lessons.isEmpty() ? lessons.first() : null;
        final TextView overview_lesson_slot2_name = mLayout.findViewById(R.id.overview_lesson_slot2_name);
        final TextView overview_lesson_slot2_room = mLayout.findViewById(R.id.overview_lesson_slot2_room);
        final TextView overview_lesson_slot2_time = mLayout.findViewById(R.id.overview_lesson_slot2_time);
        final TextView overview_lesson_slot2_art = mLayout.findViewById(R.id.overview_lesson_slot2_art);

        if (lessons != null && lessons.size() == 1 && firstLesson != null) {
            final String[] lessonType = mLayout.getResources().getStringArray(R.array.lesson_type);
            overview_lesson_slot2_name.setText(firstLesson.getName());
            overview_lesson_slot2_time.setText(TimetableHelper.getStringStartNextLesson(context, searchResult));
            overview_lesson_slot2_art.setText(lessonType[TimetableHelper.getIntegerTypOfLesson(firstLesson)]);
            String room = TimetableHelper.getStringOfRooms(firstLesson);
            if (room.isEmpty()) {
                room = context.getString(R.string.overview_lessons_no_room);
            }
            overview_lesson_slot2_room.setText(room);
        } else if (lessons != null && lessons.size() > 1) {
            overview_lesson_slot2_name.setText(R.string.timetable_moreLessons);
            overview_lesson_slot2_time.setText(TimetableHelper.getStringStartNextLesson(context, searchResult));
            overview_lesson_slot2_room.setVisibility(View.GONE);
            overview_lesson_slot2_art.setVisibility(View.GONE);
        } else {
            overview_lesson_slot2_name.setVisibility(View.GONE);
            overview_lesson_slot2_time.setVisibility(View.GONE);
            overview_lesson_slot2_room.setVisibility(View.GONE);
            overview_lesson_slot2_art.setVisibility(View.GONE);
        }
    }

    /**
     * Anzeige des Stundenübersicht
     *
     * @param calendar     {@link Calendar} mit aktueller Zeit
     * @param searchResult Suchergebnis welches nächste Stunde enthält
     */
    private void showLessonOverview(@NonNull final Calendar calendar, @NonNull final NextLessonResult searchResult) {
        if (searchResult.getResults() != null && searchResult.getResults().size() > 0) {
            final Context context = mLayout.getContext();
            final int differenceDay = Math.abs(searchResult.getStartTimeOfLesson().get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR));
            final LinearLayout overview_lessons_list = mLayout.findViewById(R.id.overview_timetable_lessons_list);
            final TextView overview_lessons_day = mLayout.findViewById(R.id.overview_timetable_lessons_day);
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
            mLayout.findViewById(R.id.overview_timetable_daily_surveying).setVisibility(View.VISIBLE);

            // Vorschau des Stundenplans erstellen
            overview_lessons_list.removeAllViews();
            TimetableHelper.createSimpleLessonOverview(context, realm, overview_lessons_list, searchResult.getStartTimeOfLesson(), currentDs);
        }
    }

    /**
     * Übersicht über Stundenplan
     */
    private void showUserTimetableOverview() {
        boolean showCard;
        LessonUser tmpLesson = null;
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);

        // Heute auf Feiertag prüfen
        TimePeriod freeDays = TimetableHelper.getFreeDayPeriod(realm, calendar);
        if (freeDays == null) {
            // aktuelle oder in der nächsten Zeit startende Veranstaltungen suchen
            RealmResults<LessonUser> tmpLessons = TimetableHelper.getLessonWithin(realm, 30);
            // Ergebnisse anzeigen
            showCard = !tmpLessons.isEmpty();
            tmpLesson = showCard ? tmpLessons.first() : null;
            showLessonInfoSlot1(tmpLesson, tmpLessons.size() > 1);
        } else {
            // Aktuell ist ein Feiertag, Hinweis anzeigen
            ((TextView) mLayout.findViewById(R.id.overview_lesson_slot1_time)).setText(R.string.timetable_overview_today);
            ((TextView) mLayout.findViewById(R.id.overview_lesson_slot1_name)).setText(freeDays.getName());
            mLayout.findViewById(R.id.overview_lesson_slot1_time).setVisibility(View.GONE);
            mLayout.findViewById(R.id.overview_lesson_slot1_art).setVisibility(View.GONE);
            showCard = true;
        }

        // Nächste Veranstaltung anhand vorherigen Ergebnis suchen
        final NextLessonResult nextLessonResult = TimetableHelper.getLessonAfter(realm, tmpLesson);
        if (nextLessonResult.getResults() != null) {
            final Calendar nextStartTime = nextLessonResult.getStartTimeOfLesson() != null ? nextLessonResult.getStartTimeOfLesson() : calendar;
            showCard = true;
            //
            freeDays = TimetableHelper.getFreeDayPeriod(realm, nextStartTime);
            if (freeDays == null) {
                showLessonInfoSlot2(nextLessonResult);
            } else {
                ((TextView) mLayout.findViewById(R.id.overview_lesson_slot2_time)).setText(R.string.timetable_overview_today);
                ((TextView) mLayout.findViewById(R.id.overview_lesson_slot2_name)).setText(freeDays.getName());
                mLayout.findViewById(R.id.overview_lesson_slot2_time).setVisibility(View.GONE);
                mLayout.findViewById(R.id.overview_lesson_slot2_art).setVisibility(View.GONE);
            }
        } else {
            // Nächste Veranstaltung ausblenden
            showLessonInfoSlot2(nextLessonResult);
        }
        showLessonOverview(calendar, nextLessonResult);

        // Karte anzeigen oder ausblenden
        mLayout.findViewById(R.id.overview_timetableNew).setVisibility(showCard ? View.VISIBLE : View.GONE);
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
