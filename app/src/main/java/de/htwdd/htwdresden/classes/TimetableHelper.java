package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.Lesson2;
import de.htwdd.htwdresden.types.Room;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * Stellt Hilfsmethoden für den Stundenplan zur Verfügung
 */
public class TimetableHelper {

    @NonNull
    public static RealmResults<Lesson2> getCurrentLessons(@NonNull final Realm realm) {
        final Calendar calendar = GregorianCalendar.getInstance();
        final long currentTime = getMinutesSinceMidnight(calendar);
        return realm.where(Lesson2.class)
                // Nach Tag einschränken
                .equalTo(Const.database.Lesson.DAY, calendar.get(Calendar.DAY_OF_WEEK) - 1)
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
     * Liefert {@link Lesson2#lessonTag} einer Lehrveranstaltung als einheitlichen int zurück
     *
     * @param lesson Stunde aus welchen der Typ/Tag bestimmt werden soll
     * @return int zur Identifikation der Art von Veranstaltung
     */
    public static int getIntegerTagOfLesson(@NonNull final Lesson2 lesson) {
        final String tag = lesson.getLessonTag();

        if (tag.startsWith("V")) {
            return Const.Timetable.TAG_VORLESUNG;
        } else if (tag.startsWith("Pr")) {
            return Const.Timetable.TAG_PRAKTIKUM;
        } else if (tag.startsWith("Ü")) {
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
        final int currentDs = Const.Timetable.getCurrentDS(getMinutesSinceMidnight(calendar));
        final long difference = getMinutesSinceMidnight(calendar) - Const.Timetable.endDS[currentDs - 1];

        if (difference < 0)
            return String.format(context.getString(R.string.overview_lessons_remaining_end), -difference);
        else
            return String.format(context.getString(R.string.overview_lessons_remaining_final), difference);
    }

    /**
     * Liefert die Räume verkettet als String zurück
     *
     * @param lesson Lehrveranstaltung aus welcher Räume ausgegeben werdern sollen
     * @return verkette Räume
     */
    public static String getStringOfRooms(@NonNull final Lesson2 lesson) {
        final RealmList<Room> rooms = lesson.getRooms();
        String roomsString = "";

        for (final Room room : rooms) {
            roomsString += room.getRoomName() + "; ";
        }
        final int roomsStringLength = roomsString.length();
        if (roomsStringLength > 2) {
            roomsString = roomsString.substring(0, roomsStringLength - 2);
        }
        return roomsString;
    }

    /**
     * Wandet {@link JSONObject} in ein für die App besser speicherbares Format um
     *
     * @param lesson {@link JSONObject} einer Lehrveranstaltung
     * @return unformiertes {@link JSONObject} einer Lehrveranstaltung
     * @throws JSONException Fehler beim Zugriff auf JSON-Daten
     */
    @NonNull
    public static JSONObject convertTimetableJsonObject(@NonNull final JSONObject lesson) throws JSONException {
        // Zeit in Minuten seit Mitternacht umrechnen
        final Calendar calendar = GregorianCalendar.getInstance();
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
     * Bestimmt ob übergebene Kalenderwoche gerade oder ungerade ist
     *
     * @param calendarWeek aktuelle Kalenderwoche
     * @return 1=ungerade KW, 2=gerade KW
     */
    private static int getWeekTyp(final int calendarWeek) {
        final int result = calendarWeek % 2;
        return result == 0 ? 2 : result;
    }

    private static long getMinutesSinceMidnight(@NonNull final Calendar calendar) {
        return TimeUnit.MINUTES.convert(calendar.get(Calendar.HOUR_OF_DAY), TimeUnit.HOURS) + calendar.get(Calendar.MINUTE);
    }
}
