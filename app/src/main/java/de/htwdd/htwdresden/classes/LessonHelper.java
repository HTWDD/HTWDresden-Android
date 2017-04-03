package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.types.Lesson;
import de.htwdd.htwdresden.types.LessonSearchResult;

/**
 * @author Kay Förster
 */
public class LessonHelper {
    private final static DateFormat DATE_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);

    /**
     * Liefert die nächste Stunde(n)
     *
     * @param context App-Context
     * @return LessonSearchResult mit Beschreibung zur nächste Stunde
     */
    @NonNull
    public static LessonSearchResult getNextUserLesson(@NonNull final Context context) {
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        final Calendar calendarNextLesson = GregorianCalendar.getInstance(Locale.GERMANY);
        final TimetableUserDAO timetableUserDAO = new TimetableUserDAO(new DatabaseManager(context));
        LessonSearchResult lessonSearchResult;

        // Aktuelle Stunde bestimmen
        int nextDS = Const.Timetable.getCurrentDS(null);

        // Vorlesungszeit vorbei? Dann auf nächsten Tag springen
        if (calendar.getTimeInMillis() > Const.Timetable.getCalendar(Const.Timetable.endDS[7 - 1]).getTimeInMillis()) {
            nextDS = 0;
            calendarNextLesson.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Suche nach einer passenden Veranstaltung
        do {
            // DS erhöhen
            if ((++nextDS) % 8 == 0) {
                nextDS = 1;
                calendarNextLesson.add(Calendar.DAY_OF_YEAR, 1);
            }

            // Lade Stunde aus DB
            ArrayList<Lesson> lessons = timetableUserDAO.getByDS(calendarNextLesson.get(Calendar.WEEK_OF_YEAR), calendarNextLesson.get(Calendar.DAY_OF_WEEK) - 1, nextDS);

            // Suche nach passender Stunde
            lessonSearchResult = LessonHelper.searchLesson(lessons, calendarNextLesson.get(Calendar.WEEK_OF_YEAR));
        }
        // Suche solange nach einer passenden Stunde bis eine Stunde gefunden wurde. Nach über zwei Tagen wird die Suche abgebrochen
        while (lessonSearchResult.getCode() == Const.Timetable.NO_LESSON_FOUND && (calendarNextLesson.get(Calendar.WEEK_OF_YEAR) - calendar.get(Calendar.WEEK_OF_YEAR)) < 2);

        // Wenn keine Stunde gefunden wurde kann hier abgebrochen werden, ansonsten Anzahl setzen
        if (lessonSearchResult.getCode() == Const.Timetable.NO_LESSON_FOUND)
            return lessonSearchResult;

        //Zeit-Abstand berechen und Stunde anzeigen
        int differenceDay = Math.abs(calendarNextLesson.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR));
        switch (differenceDay) {
            case 0:
                long minuten = TimeUnit.MINUTES.convert(
                        Const.Timetable.getCalendar(Const.Timetable.beginDS[nextDS - 1]).getTimeInMillis() - calendar.getTimeInMillis(),
                        TimeUnit.MILLISECONDS
                );
                lessonSearchResult.setTimeRemaining(String.format(context.getString(R.string.overview_lessons_remaining_start), minuten));
                break;
            case 1:
                lessonSearchResult.setTimeRemaining(context.getString(
                        R.string.overview_lessons_tomorrow_param,
                        context.getString(
                                R.string.timetable_ds_list_simple,
                                DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.beginDS[nextDS - 1])),
                                DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.endDS[nextDS - 1])))
                ));
                break;
            default:
                final String[] nameOfDays = DateFormatSymbols.getInstance().getWeekdays();
                lessonSearchResult.setTimeRemaining(context.getString(
                        R.string.overview_lessons_future,
                        nameOfDays[calendarNextLesson.get(Calendar.DAY_OF_WEEK)],
                        context.getString(
                                R.string.timetable_ds_list_simple,
                                DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.beginDS[nextDS - 1])),
                                DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.endDS[nextDS - 1])))
                ));
                break;
        }
        lessonSearchResult.setCalendar(calendarNextLesson);
        return lessonSearchResult;
    }

    /**
     * @param lessons Liste der zu überprüfenden Stunden
     * @param week    KW in der die zu suchende Veranstaltung stattfindet
     * @return 0=keine passende Stunden gefunden, 1=eine Stunden gefunden, 2=mehrere Stunden gefunden
     */
    @NonNull
    public static LessonSearchResult searchLesson(@NonNull ArrayList<Lesson> lessons, int week) {
        LessonSearchResult lessonSearchResult = new LessonSearchResult();

        // Suche nach einer passenden Veranstaltung
        for (Lesson tmp : lessons) {
            // Es ist keine spezielle KW gesetzt, d.h. die Veranstaltung ist immer
            if (tmp.getWeeksOnly().isEmpty()) {
                lessonSearchResult.setCode(lessonSearchResult.getCode() + 1);

                // Es sind spezielle KW gestzt, suche aktuelle zum anzeigen
                if (lessonSearchResult.getCode() == Const.Timetable.ONE_LESSON_FOUND) {
                    lessonSearchResult.setLesson(tmp);
                } else {
                    // Zweite Veranstallung gefunden, die "immer" ist, weitersuchen sinnlos
                    lessonSearchResult.setLesson(null);
                    break;
                }
            }

            // Es sind spezielle KW gestzt, suche aktuelle zum anzeigen
            String[] lessonWeek = tmp.getWeeksOnly().split(";");

            // Aktuelle Woche enthalten?
            if (Arrays.asList(lessonWeek).contains(String.valueOf(week))) {
                lessonSearchResult.setCode(lessonSearchResult.getCode() + 1);

                if (lessonSearchResult.getCode() == Const.Timetable.ONE_LESSON_FOUND) {
                    lessonSearchResult.setLesson(tmp);
                } else {
                    // Zweite Veranstallung gefunden, die "immer" ist
                    lessonSearchResult.setLesson(null);
                    break;
                }
            }
        }
        return lessonSearchResult;
    }

    /**
     * Erstellt aus einem JSON-Array von Stunden eine Liste von Stunden-Objekten
     *
     * @param response JSON-Array welches die Stunden enthält
     * @return Liste mit Stunden-Objekten
     * @throws JSONException
     * @throws CloneNotSupportedException
     */
    public static ArrayList<Lesson> getList(@NonNull JSONArray response) throws JSONException, CloneNotSupportedException {
        int count = response.length();
        ArrayList<Lesson> lessons = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Lesson lesson = new Lesson();
            lesson.parseFromJSON(response.getJSONObject(i));
            lessons.add(lesson);
            long endTime = lesson.getEndTime();

            // Bestimme End DS und ggf neu eintragen
            switch (lesson.getDs()) {
                case 1:
                    if (endTime <= Const.Timetable.beginDS[1])
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(2);
                    lessons.add(lesson);
                case 2:
                    if (endTime <= Const.Timetable.beginDS[2])
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(3);
                    lessons.add(lesson);
                case 3:
                    if (endTime <= Const.Timetable.beginDS[3])
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(4);
                    lessons.add(lesson);
                case 4:
                    if (endTime <= Const.Timetable.beginDS[4])
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(5);
                    lessons.add(lesson);
                case 5:
                    if (endTime <= Const.Timetable.beginDS[5])
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(6);
                    lessons.add(lesson);
                case 6:
                    if (endTime <= Const.Timetable.beginDS[6])
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(7);
                    lessons.add(lesson);
            }
        }
        return lessons;
    }

    /**
     * Erstellt eine Stundenplanvorschau
     *
     * @param context      alktueller App-Context
     * @param linearLayout View in welchem die Stundenplanvorschau erzeugt wird
     * @param viewGroup    Das Eltern-View-Element
     * @param timetable    String-Array welches angezeigt wird
     * @param current_ds   aktuelle DS oder 0 wenn außerhalb der Lehrveranstaltungszeiten
     */
    public static void createSimpleDayOverviewLayout(@NonNull final Context context,
                                                     @NonNull final LinearLayout linearLayout,
                                                     @Nullable final ViewGroup viewGroup,
                                                     @NonNull final String[] timetable,
                                                     int current_ds) {
        final LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final String[] listOfDs = new String[Const.Timetable.beginDS.length];
        int count = listOfDs.length;
        Resources resources = context.getResources();
        for (int i = 0; i < count; i++)
            listOfDs[i] = resources.getString(
                    R.string.timetable_ds_list_simple,
                    DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.beginDS[i])),
                    DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.endDS[i]))
            );

        // Alle vorhanden Views entfernen
        linearLayout.removeAllViews();

        // Tagesüberblick anzeigen
        int index = 0;
        for (String lessonDs : listOfDs) {
            View sub_view = mLayoutInflater.inflate(R.layout.fragment_timetable_mini_plan, viewGroup, false);

            // Hintergrund einfärben
            if (index == (current_ds - 1))
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.timetable_blue));
            else if (index % 2 == 0)
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.app_background));
            else
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

            // Zeiten anzeigen
            TextView textDS = (TextView) sub_view.findViewById(R.id.timetable_busy_plan_ds);
            textDS.setText(lessonDs);

            // Stunde anzeigen
            TextView textLesson = (TextView) sub_view.findViewById(R.id.timetable_busy_plan_lesson);
            textLesson.setText(timetable[index]);

            // View zum LinearLayout hinzufügen
            linearLayout.addView(sub_view);

            // Index erhöhen
            index++;
        }
    }
}
