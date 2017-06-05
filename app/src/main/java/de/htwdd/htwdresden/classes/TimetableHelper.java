package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.Lesson2;
import de.htwdd.htwdresden.types.LessonWeek;
import de.htwdd.htwdresden.types.Room;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static de.htwdd.htwdresden.classes.Const.Timetable.endDS;


/**
 * Stellt Hilfsmethoden für den Stundenplan zur Verfügung
 *
 * @author Kay Förster
 */
public class TimetableHelper extends AbstractTimetableHelper {

    /**
     * Liefert eine Liste der aktuell laufenden Lehrveranstaltungen
     *
     * @param realm aktuelle Datenbankverbindung
     * @return Liste von aktuell laufenden Lehrveranstaltungen. Finden aktuell keine statt ist die Liste leer
     */
    @NonNull
    public static RealmResults<Lesson2> getCurrentLessons(@NonNull final Realm realm) {
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        final long currentTime = getMinutesSinceMidnight(calendar);
        return realm.where(Lesson2.class)
                // Nach Tag einschränken
                .equalTo(Const.database.Lesson.DAY, calendar.get(Calendar.DAY_OF_WEEK) - 1)
                // Nicht ausgeblendet
                .equalTo(Const.database.Lesson.HIDE_LESSON, false)
                // Nach Kalenderwoche einschränken
                .beginGroup()
                .equalTo(Const.database.Lesson.WEEK, getWeekTyp(calendar.get(Calendar.WEEK_OF_YEAR)))
                .or().equalTo(Const.database.Lesson.WEEK, 0)
                .endGroup()
                // Nach Zeit einschränken
                .greaterThan(Const.database.Lesson.END_TIME, currentTime)
                .lessThan(Const.database.Lesson.BEGIN_TIME, currentTime)
                // Einzelne Wochen ausschließen
                .beginGroup()
                .isEmpty(Const.database.Lesson.WEEKS_ONLY)
                .or().equalTo(Const.database.Lesson.WEEKS_ONLY + ".weekOfYear", calendar.get(Calendar.WEEK_OF_YEAR))
                .endGroup()
                .findAll();
    }

    /**
     * Liefert eine List der Lehrveranstaltungen des übergebenen Tages und Ds
     *
     * @param realm aktuelle Datenbankverbindung
     * @param calendar Tag für welchen die Lehrveranstaltungen gelistet werden soll
     * @param ds Zeit in welcher die Lehrveranstaltungen stattfinden sollen
     * @param filterCurrentWeek Nur Lehrveranstaltungen der aktuellen Kalenderwoche zurückgeben
     * @param showHiddenLessons versteckte Lehrveranstaltungen mit anzeigen
     * @return Liste von passenden Lehrveranstaltungen
     */
    public static RealmResults<Lesson2> getLessonsByDateAndDs(@NonNull final Realm realm, @NonNull final Calendar calendar, final int ds, final boolean filterCurrentWeek,
                                                              final boolean showHiddenLessons) {
        final int dsIndex = ds > 0 ? ds - 1 : 0;
        final RealmQuery<Lesson2> realmQuery = realm.where(Lesson2.class)
                .equalTo(Const.database.Lesson.DAY, calendar.get(Calendar.DAY_OF_WEEK) - 1)
                // Nach Kalenderwoche einschränken
                .beginGroup()
                .equalTo(Const.database.Lesson.WEEK, getWeekTyp(calendar.get(Calendar.WEEK_OF_YEAR)))
                .or().equalTo(Const.database.Lesson.WEEK, 0)
                .endGroup()
                // Vor dem Ende dieser DS beginnen und länger gehen als DS startet
                .lessThan(Const.database.Lesson.BEGIN_TIME, Const.Timetable.endDS[dsIndex])
                .greaterThan(Const.database.Lesson.END_TIME, Const.Timetable.beginDS[dsIndex]);

        if (filterCurrentWeek) {
            realmQuery.beginGroup()
                    .isEmpty(Const.database.Lesson.WEEKS_ONLY)
                    .or().equalTo(Const.database.Lesson.WEEKS_ONLY + ".weekOfYear", calendar.get(Calendar.WEEK_OF_YEAR))
                    .endGroup();
        }

        // Versteckte Lehrveranstaltungen ausschließen
        if (!showHiddenLessons) {
            realmQuery.equalTo(Const.database.Lesson.HIDE_LESSON, false);
        }

        return realmQuery.findAll();
    }

    /**
     * Liefert die nächste stattfinde Lehrveranstaltungen
     *
     * @param realm aktuelle Datenbankverbindung
     * @return {@link NextLessonResult} mit Informationen zur nächsten Lehrveranstaltung
     */
    public static NextLessonResult getNextLessons(@NonNull final Realm realm) {
        final NextLessonResult lessonResult = new NextLessonResult(GregorianCalendar.getInstance(Locale.GERMANY));

        // Suche Lehrveranstaltung in der aktuellen Woche
        Lesson2 startLesson = getStartNextLessonInWeek(realm, lessonResult.getOnNextDay());
        // Suche Lehrveranstaltung in der nächsten Woche
        if (startLesson == null) {
            final Calendar calendar = lessonResult.getOnNextDay();
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            startLesson = getStartNextLessonInWeek(realm, calendar);
        }

        if (startLesson != null) {
            final int nextDs = getCurrentDS((long) startLesson.getBeginTime());
            lessonResult.getOnNextDay().set(Calendar.DAY_OF_WEEK, startLesson.getDay() + 1);

            // Da in der passenden Zeit mehrere Veranstaltungen stattfinden können, diese suchen
            final RealmResults<Lesson2> results = realm.where(Lesson2.class)
                    .equalTo(Const.database.Lesson.HIDE_LESSON, false)
                    .equalTo(Const.database.Lesson.DAY, startLesson.getDay())
                    .greaterThanOrEqualTo(Const.database.Lesson.BEGIN_TIME, startLesson.getBeginTime())
                    .lessThan(Const.database.Lesson.BEGIN_TIME, endDS[nextDs > 0 ? nextDs - 1 : nextDs])
                    .beginGroup()
                    .isEmpty(Const.database.Lesson.WEEKS_ONLY)
                    .or().equalTo(Const.database.Lesson.WEEKS_ONLY + ".weekOfYear", lessonResult.getOnNextDay().get(Calendar.WEEK_OF_YEAR))
                    .endGroup()
                    .findAllSorted(new String[]{Const.database.Lesson.DAY, Const.database.Lesson.BEGIN_TIME}, new Sort[]{Sort.ASCENDING, Sort.ASCENDING});
            lessonResult.setResults(results);
        }
        return lessonResult;
    }

    /**
     * Liefert {@link Lesson2#lessonTag} einer Lehrveranstaltung als einheitlichen int zurück
     *
     * @param lesson Stunde aus welchen der Typ/Tag bestimmt werden soll
     * @return int zur Identifikation der Art von Veranstaltung
     */
    public static int getIntegerTypOfLesson(@NonNull final Lesson2 lesson) {
        final String type = lesson.getType();

        if (type.startsWith("V")) {
            return Const.Timetable.TAG_VORLESUNG;
        } else if (type.startsWith("Pr")) {
            return Const.Timetable.TAG_PRAKTIKUM;
        } else if (type.startsWith("Ü")) {
            return Const.Timetable.TAG_UBUNG;
        } else return Const.Timetable.TAG_OTHER;
    }

    /**
     * Text wie lange die aktuelle Lehrveranstaltung noch geht
     *
     * @param context aktueller App-Context
     * @return String wie lange eine Lehrveranstaltung noch geht
     */
    public static String getStringRemainingTime(@NonNull final Context context) {
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        final int currentDs = getCurrentDS(getMinutesSinceMidnight(calendar));
        final long difference = getMinutesSinceMidnight(calendar) - endDS[currentDs - 1];

        if (difference < 0)
            return String.format(context.getString(R.string.overview_lessons_remaining_end), -difference);
        else
            return String.format(context.getString(R.string.overview_lessons_remaining_final), difference);
    }

    /**
     * Text wann die nächste Lehrveranstaltung startet
     *
     * @param context          aktueller App-Context
     * @param nextLessonResult Informationen zu den nächsten Lehrveranstaltungen
     * @return String wenn die nächste Lehrveranstaltung startet
     */
    public static String getStringStartNextLesson(@NonNull final Context context, @NonNull final NextLessonResult nextLessonResult) {
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        final int differenceDay = Math.abs(nextLessonResult.getOnNextDay().get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR));

        if (nextLessonResult.getResults() == null)
            return "";

        final int nextDS = getCurrentDS(nextLessonResult.getResults().first().getBeginTime());

        switch (differenceDay) {
            case 0:
                final long minuten = Const.Timetable.beginDS[nextDS - 1] - getMinutesSinceMidnight(calendar);
                return String.format(context.getString(R.string.overview_lessons_remaining_start), minuten);
            case 1:
                return context.getString(
                        R.string.overview_lessons_tomorrow_param,
                        context.getString(
                                R.string.timetable_ds_list_simple,
                                DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.beginDS[nextDS - 1])),
                                DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.endDS[nextDS - 1]))
                        )
                );
            default:
                final String[] nameOfDays = DateFormatSymbols.getInstance().getWeekdays();
                return context.getString(
                        R.string.overview_lessons_future,
                        nameOfDays[nextLessonResult.getOnNextDay().get(Calendar.DAY_OF_WEEK)],
                        context.getString(
                                R.string.timetable_ds_list_simple,
                                DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.beginDS[nextDS - 1])),
                                DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.endDS[nextDS - 1]))
                        )
                );
        }
    }

    /**
     * Liefert die Räume verkettet als String zurück
     *
     * @param lesson Lehrveranstaltung aus welcher Räume ausgegeben werden sollen
     * @return verkette Räume
     */
    public static String getStringOfRooms(@NonNull final Lesson2 lesson) {
        String roomsString = "";

        final RealmList<Room> rooms = lesson.getRooms();
        for (final Room room : rooms) {
            roomsString += room.getRoomName() + "; ";
        }
        return removeLastComma(roomsString);
    }

    /**
     * Liefert die Kalenderwochen verkettet als String zurück
     *
     * @param lesson Lehrveranstaltung aus welcher Kalenderwochen ausgegeben werden sollen
     * @return verkettete Kalenderwochen
     */
    public static String getStringOfKws(@NonNull final Lesson2 lesson) {
        String weeks = "";

        final RealmList<LessonWeek> lessonWeeks = lesson.getWeeksOnly();
        for (final LessonWeek lessonWeek : lessonWeeks) {
            weeks += lessonWeek.getWeekOfYear() + "; ";
        }
        return removeLastComma(weeks);
    }

    /**
     * Erstellt eine Liste von Lehrveranstaltungen des Tages
     *
     * @param context      aktueller App-Context
     * @param realm        aktuelle Datenbankverbindung
     * @param linearLayout {@link LinearLayout} in welchem die Liste eingefügt wird
     * @param day          Tag für welchen die Übersicht erstellt werden soll
     * @param current_ds   aktuelle DS welche hervorgehoben werden soll,sonst 0
     */
    public static void createSimpleLessonOverview(@NonNull final Context context, final Realm realm, @NonNull final LinearLayout linearLayout, final Calendar day,
                                                  final int current_ds) {
        final int countUnits = Const.Timetable.beginDS.length;
        final List<RealmResults<Lesson2>> realmResultsList = new ArrayList<>(countUnits);
        for (int i = 0; i < countUnits; i++) {
            realmResultsList.add(getLessonsByDateAndDs(realm, day, i + 1, true, false));
        }
        createSimpleLessonOverview(context, realmResultsList, linearLayout, current_ds);
    }

    /**
     * Wandet {@link JSONObject} in ein für die App besseres Format zum Speichern um
     *
     * @param lesson {@link JSONObject} einer Lehrveranstaltung
     * @return umstrukturiertes {@link JSONObject} einer Lehrveranstaltung
     * @throws JSONException Fehler beim Zugriff auf JSON-Daten
     */
    @NonNull
    public static JSONObject convertTimetableJsonObject(@NonNull final JSONObject lesson) throws JSONException {
        // Zeit in Minuten seit Mitternacht umrechnen
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        final long beginTime = Time.valueOf(lesson.getString("beginTime")).getTime();
        final long endTime = Time.valueOf(lesson.getString("endTime")).getTime();
        lesson.put("beginTime", TimeUnit.MINUTES.convert(beginTime + calendar.getTimeZone().getOffset(beginTime), TimeUnit.MILLISECONDS));
        lesson.put("endTime", TimeUnit.MINUTES.convert(endTime + calendar.getTimeZone().getOffset(endTime), TimeUnit.MILLISECONDS));

        // Array von primitiven Typen in Objekte umwandeln
        lesson.put("weeksOnly", convertPrimitivTypToJsonObject(lesson.getJSONArray("weeksOnly"), "weekOfYear"));
        lesson.put("rooms", convertPrimitivTypToJsonObject(lesson.getJSONArray("rooms"), "roomName"));

        return lesson;
    }

    /**
     * Entfernt das letzte Leerzeichen und Komma von einer verketten Aufzählung
     *
     * @param s verkette Aufzählung
     * @return verkette Aufzählung ohne letztes Komma und Leerzeichen
     */
    private static String removeLastComma(@NonNull String s) {
        final int length = s.length();
        if (length >= 2) {
            s = s.substring(0, length - 2);
        }
        return s;
    }

    /**
     * Wandelt eine {@link JSONArray} von primitiven Typen in ein {@link JSONArray} von Objekten um
     *
     * @param array {@link JSONArray} von primitiven Typen
     * @param type  Name des Objektes
     * @return JSONArray mit Objekten des primitiven Typs
     * @throws JSONException Fehler beim Zugriff auf das {@link JSONArray}
     */
    @NonNull
    private static JSONArray convertPrimitivTypToJsonObject(@NonNull final JSONArray array, @NonNull final String type) throws JSONException {
        final int count = array.length();
        final JSONArray result = new JSONArray();

        JSONObject jsonObject;
        for (int i = 0; i < count; i++) {
            jsonObject = new JSONObject();
            jsonObject.put(type, array.get(i));
            result.put(jsonObject);
        }

        return result;
    }

    /**
     * Sucht nach der ersten passenden Lehrveranstaltung die als nächstes in der aktuellen Woche stattfindet
     *
     * @param realm    aktuelle Datenbankverbindung
     * @param calendar aktuelle Zeitpunkt ab welchem gesucht werden soll
     * @return erste passende {@link Lesson2} oder null
     */
    @Nullable
    private static Lesson2 getStartNextLessonInWeek(@NonNull final Realm realm, @NonNull final Calendar calendar) {
        final long currentTime = getMinutesSinceMidnight(calendar);
        final int currentDs = getCurrentDS(currentTime);

        // Nur Lehrveranstaltungen suchen die nicht ausgeblendet werden sollen
        final RealmQuery<Lesson2> realmQuery = realm.where(Lesson2.class).equalTo(Const.database.Lesson.HIDE_LESSON, false);

        // Lehrveranstaltungen auf aktuelle Woche einschränken
        realmQuery.beginGroup()
                .equalTo(Const.database.Lesson.WEEK, getWeekTyp(calendar.get(Calendar.WEEK_OF_YEAR)))
                .or()
                .equalTo(Const.database.Lesson.WEEK, 0)
                .endGroup()
                .beginGroup()
                .isEmpty(Const.database.Lesson.WEEKS_ONLY)
                .or().equalTo(Const.database.Lesson.WEEKS_ONLY + ".weekOfYear", calendar.get(Calendar.WEEK_OF_YEAR))
                .endGroup();

        // Veranstaltungen in der restlichen Woche
        realmQuery.beginGroup()
                // Veranstaltungen in der restlichen Woche
                .greaterThan(Const.database.Lesson.DAY, calendar.get(Calendar.DAY_OF_WEEK) - 1);

        // Heutige Lehrveranstaltungen innerhalb der Vorlesungszeit mit berücksichtigen
        if (currentDs > 0) {
            realmQuery.or()
                    // Veranstaltungen die noch heute stattfinden, aber...
                    .beginGroup()
                    .equalTo(Const.database.Lesson.DAY, calendar.get(Calendar.DAY_OF_WEEK) - 1)
                    .beginGroup()
                    // ... erst nach dieser Stunde anfangen ...
                    .greaterThan(Const.database.Lesson.BEGIN_TIME, endDS[currentDs - 1])
                    .or()
                    // ... oder erst nach dieser Stunde enden
                    .beginGroup()
                    .greaterThan(Const.database.Lesson.END_TIME, endDS[currentDs - 1])
                    .lessThan(Const.database.Lesson.END_TIME, currentTime)
                    .endGroup()
                    .endGroup()
                    .endGroup();
        }
        // Heutige Lehrveranstaltungen vor der der Vorlesungszeit berücksichtigen
        else if (currentDs == 0) {
            realmQuery.or()
                    .equalTo(Const.database.Lesson.DAY, calendar.get(Calendar.DAY_OF_WEEK) - 1);
        }
        realmQuery.endGroup();

        // Ergebnisse sortieren und erste Stunde bestimmen
        final RealmResults<Lesson2> results = realmQuery.findAllSorted(Const.database.Lesson.DAY, Sort.ASCENDING, Const.database.Lesson.BEGIN_TIME, Sort.ASCENDING);
        return results.size() > 0 ? results.first() : null;
    }
}
