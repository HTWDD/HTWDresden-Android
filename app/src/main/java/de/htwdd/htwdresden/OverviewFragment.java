package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
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

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.classes.ExamsHelper;
import de.htwdd.htwdresden.classes.LessonHelper;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.events.UpdateTimetableEvent;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.ExamResult;
import de.htwdd.htwdresden.types.ExamStats;
import de.htwdd.htwdresden.types.Lesson;
import de.htwdd.htwdresden.types.LessonSearchResult;
import de.htwdd.htwdresden.types.Meal;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Fragment für den Schnelleinstieg in die App
 */
public class OverviewFragment extends Fragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_overview, container, false);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Setze Toolbartitle
        ((INavigation) getActivity()).setTitle(getResources().getString(R.string.navi_overview));

        // Update verfügbar
        final CardView cardUpdate = (CardView) mLayout.findViewById(R.id.overview_app_update);
        cardUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www2.htw-dresden.de/~app/android/HTWDresden-latest.apk"));
                startActivity(browserIntent);
            }
        });
        if (sharedPreferences.getBoolean("appUpdate", false))
            cardUpdate.setVisibility(View.VISIBLE);

        // Stundenplan
        final CardView cardTimetable = (CardView) mLayout.findViewById(R.id.overview_timetable);
        cardTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((INavigation) getActivity()).goToNavigationItem(R.id.navigation_timetable);
            }
        });

        // Navigation zur Mensa
        final CardView cardMensa = (CardView) mLayout.findViewById(R.id.overview_mensa);
        cardMensa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((INavigation) getActivity()).goToNavigationItem(R.id.navigation_mensa);
            }
        });

        // Noten
        final CardView cardExam = (CardView) mLayout.findViewById(R.id.overview_examResultStats);
        cardExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((INavigation) getActivity()).goToNavigationItem(R.id.navigation_exams);
            }
        });

        // Daten für Mensa laden und anzeigen
        final Realm realm = Realm.getDefaultInstance();
        final Calendar calendar = GregorianCalendar.getInstance();
        final RealmResults<Meal> meals = realm.where(Meal.class).equalTo("date", MensaHelper.getDate(calendar)).findAll();
        meals.addChangeListener(new RealmChangeListener<RealmResults<Meal>>() {
            @Override
            public void onChange(final RealmResults<Meal> element) {
                showMensaInfo(meals);
            }
        });
        showMensaInfo(meals);

        // Übersicht über Noten
        realm.where(ExamResult.class).findAll().addChangeListener(new RealmChangeListener<RealmResults<ExamResult>>() {
            @Override
            public void onChange(final RealmResults<ExamResult> element) {
                showExamStats(element.size() > 0);
            }
        });
        showExamStats(realm.where(ExamResult.class).count() > 0);

        // Stundenplan anzeigen
        updateTimetable(null);

        // News laden
        showNews();

        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimetable(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getInstance().unregister(this);
    }

    /**
     * Zeigt eine Gesamtstatistik der Noten an oder einen entsprechenden Hinweis
     *
     * @param examsAvailable Sind Noten vorhanden?
     */
    private void showExamStats(final boolean examsAvailable) {
        final TextView message = (TextView) mLayout.findViewById(R.id.overview_examResultStatsMessage);
        final LinearLayout content = (LinearLayout) mLayout.findViewById(R.id.overview_examResultStatsContent);

        if (examsAvailable) {
            message.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);

            // Erstelle Statistik
            final ExamStats examStats = ExamsHelper.getExamStatsForSemester(Realm.getDefaultInstance(), null);

            // Views holen
            final Context context = getActivity();
            final TextView stats_average = (TextView) mLayout.findViewById(R.id.stats_average);
            final TextView stats_countGrade = (TextView) mLayout.findViewById(R.id.stats_countGrade);
            final TextView stats_countCredits = (TextView) mLayout.findViewById(R.id.stats_countCredits);
            final TextView stats_gradeBest = (TextView) mLayout.findViewById(R.id.stats_gradeBest);
            final TextView stats_gradeWorst = (TextView) mLayout.findViewById(R.id.stats_gradeWorst);

            stats_average.setText(context.getString(R.string.exams_stats_average, String.format(Locale.getDefault(), "%.2f", examStats.getAverage())));
            stats_countGrade.setText(context.getResources().getQuantityString(R.plurals.exams_stats_count_grade, (int) examStats.gradeCount, (int) examStats.gradeCount));
            stats_countCredits.setText(context.getString(R.string.exams_stats_count_credits, examStats.getCredits()));
            stats_gradeBest.setText(context.getString(R.string.exams_stats_gradeBest, examStats.getGradeBest()));
            stats_gradeWorst.setText(context.getString(R.string.exams_stats_gradeWorst, examStats.getGradeWorst()));
        } else {
            message.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        }
    }

    /**
     * Behandelt die Benachrichtigung vom Eventbus das ein neuer Stundenplan zur Verfügung steht
     *
     * @param updateTimetableEvent Typ der Benachrichtigung
     */
    @Subscribe
    public void updateTimetable(@Nullable UpdateTimetableEvent updateTimetableEvent) {
        final Context context = getActivity();
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        final TimetableUserDAO timetableUserDAO = new TimetableUserDAO(new DatabaseManager(getActivity()));
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
        final Calendar calendarNextLesson = lessonSearchResult.getCalendar();
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
            final String[] values = new String[7];
            for (int i = 1; i < 8; i++) {
                final ArrayList<Lesson> lessons = timetableUserDAO.getByDS(calendarNextLesson.get(Calendar.WEEK_OF_YEAR), calendarNextLesson.get(Calendar.DAY_OF_WEEK) - 1, i);

                // Suche nach passender Stunde
                final LessonSearchResult lessonSearchResult_vorschau = LessonHelper.searchLesson(lessons, calendar.get(Calendar.WEEK_OF_YEAR));

                switch (lessonSearchResult_vorschau.getCode()) {
                    case Const.Timetable.NO_LESSON_FOUND:
                        values[i - 1] = "";
                        break;
                    case Const.Timetable.ONE_LESSON_FOUND:
                        final Lesson lesson_vorschau = lessonSearchResult_vorschau.getLesson();
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

    /**
     * Zeigt die Mahlzeiten als einfache Liste an
     * @param meals Liste der Mahlzeiten
     */
    private void showMensaInfo(@NonNull final RealmResults<Meal> meals) {
        final TextView message = (TextView) mLayout.findViewById(R.id.overview_mensaMessage);
        final TextView content = (TextView) mLayout.findViewById(R.id.overview_mensaContent);
        final int countMeals = meals.size();

        // Aktuell kein Angebot vorhanden
        if (countMeals == 0) {
            message.setText(R.string.mensa_no_offer);
            message.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            return;
        }

        // Anzeige zusammenbauen
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < countMeals - 1; i++) {
            stringBuilder.append(meals.get(i).getTitle());
            stringBuilder.append("\n\n");
        }
        stringBuilder.append(meals.get(countMeals - 1).getTitle());

        // Inhalt anzeigen
        content.setText(stringBuilder);
        message.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

    private void showNews() {
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("https://www2.htw-dresden.de/~app/API/GetNews.php", new Response.Listener<JSONArray>() {
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
}
