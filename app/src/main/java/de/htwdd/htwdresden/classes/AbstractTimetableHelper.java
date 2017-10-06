package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.interfaces.ILesson;
import de.htwdd.htwdresden.service.TimetableProfessorSyncService;
import de.htwdd.htwdresden.service.TimetableStudentSyncService;
import io.realm.RealmModel;
import io.realm.RealmResults;

import static de.htwdd.htwdresden.classes.Const.Timetable.beginDS;
import static de.htwdd.htwdresden.classes.Const.Timetable.endDS;

/**
 * Stellt allgemeine Hilfsmethoden für Lehrveranstaltungen bereit
 *
 * @author Kay Förster
 */
abstract class AbstractTimetableHelper {
    final static DateFormat DATE_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);

    /**
     * Liefert die Minuten seit Mitternacht aus dem übergeben {@see Calendar}
     *
     * @param calendar Datum / Zeit
     * @return Minuten seit Mitternacht
     */
    public static long getMinutesSinceMidnight(@NonNull final Calendar calendar) {
        return TimeUnit.MINUTES.convert(calendar.get(Calendar.HOUR_OF_DAY), TimeUnit.HOURS) + calendar.get(Calendar.MINUTE);
    }

    /**
     * Liefert die aktuelle DS zur übergeben Zeit
     *
     * @param currentTime Minuten seit Mitternacht
     * @return die aktuelle DS, 0 falls vor der ersten DS oder -1 nach der letzten DS
     */
    public static int getCurrentDS(long currentTime) {
        if (currentTime >= endDS[6])
            return -1;
        else if (currentTime >= beginDS[6])
            return 7;
        else if (currentTime >= beginDS[5])
            return 6;
        else if (currentTime >= beginDS[4])
            return 5;
        else if (currentTime >= beginDS[3])
            return 4;
        else if (currentTime >= beginDS[2])
            return 3;
        else if (currentTime >= beginDS[1])
            return 2;
        else if (currentTime >= beginDS[0])
            return 1;

        return 0;
    }

    /**
     * Liefert die Kurz-Tag einer Lehrveranstaltung als einheitlichen int zurück
     *
     * @param lesson Stunde aus welchen der Typ/Tag bestimmt werden soll
     * @return int zur Identifikation der Art von Veranstaltung
     */
    public static int getIntegerTypOfLesson(@NonNull final ILesson lesson) {
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
     * Überprüft Einstellungen und startet dann den jeweiligen Stundenplan-SyncService
     *
     * @param context Aktueller App-Context
     * @return true wenn SyncService gestartet, false bei fehlenden Einstellungen
     */
    public static boolean startSyncService(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Überprüfe Einstellungen für Professoren (zuerst, da Studenten ihre Entscheidung nicht mehr löschen können)
        if (sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_PROFESSOR, "").length() != 0) {
            Log.d("AbstractTimetableHelper", "Starte TimetableProfessorSyncService");
            context.startService(new Intent(context, TimetableProfessorSyncService.class));
            return true;
        }
        // Überprüfe Einstellungen für Studenten
        else if (sharedPreferences.contains(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR)
                && sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGANG, "").length() == 3
                && sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGRUPPE, "").length() != 0) {
            Log.d("AbstractTimetableHelper", "Starte TimetableStudentSyncService");
            context.startService(new Intent(context, TimetableStudentSyncService.class));
            return true;
        }
        return false;
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
    static int getWeekTyp(final int calendarWeek) {
        final int result = calendarWeek % 2;
        return result == 0 ? 2 : result;
    }

    /**
     * Entfernt das letzte Leerzeichen und Komma von einer verketten Aufzählung
     *
     * @param s verkette Aufzählung
     * @return verkette Aufzählung ohne letztes Komma und Leerzeichen
     */
    static String removeLastComma(@NonNull String s) {
        final int length = s.length();
        if (length >= 2) {
            s = s.substring(0, length - 2);
        }
        return s;
    }

    /**
     * Erstellt eine Liste von Lehrveranstaltungen des Tages
     *
     * @param context      aktueller App-Context
     * @param linearLayout {@link LinearLayout} in welchem die Liste eingefügt wird
     * @param current_ds   aktuelle DS welche hervorgehoben werden soll,sonst 0
     * @param <T>          {@link ILesson}
     */
    static <T extends RealmModel & ILesson> void createSimpleLessonOverview(@NonNull final Context context, @NonNull final List<RealmResults<T>> iLessons,
                                                                            @NonNull final LinearLayout linearLayout, final int current_ds) {
        final LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Resources resources = context.getResources();
        int iteration = 0;
        int foundedLessons;

        for (final RealmResults<T> lessons : iLessons) {
            final View sub_view = mLayoutInflater.inflate(R.layout.fragment_timetable_mini_plan, linearLayout, false);

            // Hintergrund einfärben
            if (iteration == (current_ds - 1))
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.timetable_blue));
            else if (iteration % 2 == 0)
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.app_background));
            else
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

            // Zeiten anzeigen
            final TextView textDS = sub_view.findViewById(R.id.timetable_busy_plan_ds);
            textDS.setText(resources.getString(
                    R.string.timetable_ds_list_simple,
                    DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.beginDS[iteration])),
                    DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.endDS[iteration]))
            ));

            // Stunde anzeigen
            foundedLessons = lessons.size();
            final TextView textLesson = sub_view.findViewById(R.id.timetable_busy_plan_lesson);
            switch (foundedLessons) {
                case 0:
                    textLesson.setText("");
                    break;
                case 1:
                    final ILesson lesson = lessons.first();
                    if (lesson.getLessonTag() != null) {
                        textLesson.setText(context.getResources().getString(R.string.timetable_overview_lessons, lesson.getLessonTag(), lesson.getType()));
                    } else {
                        textLesson.setText(lesson.getType());
                    }
                    break;
                default:
                    textLesson.setText(R.string.timetable_moreLessons);
                    break;
            }

            // View zum LinearLayout hinzufügen
            linearLayout.addView(sub_view);
            iteration++;
        }
    }
}
