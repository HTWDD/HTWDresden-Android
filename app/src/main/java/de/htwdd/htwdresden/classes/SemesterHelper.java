package de.htwdd.htwdresden.classes;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Stellt Hilfsmethoden für das Semester bereit
 *
 * @author Kay Förster
 */
public class SemesterHelper {

    /**
     * Formatiert die Daten eines {@link JSONArray} vom Typ {@link de.htwdd.htwdresden.types.semsterPlan.Semester} in ein Format zum Speicher um
     *
     * @param semesterPlan {@link JSONArray} welches {@link de.htwdd.htwdresden.types.semsterPlan.Semester} entspricht
     * @return {@link JSONArray} vom Typ {@link de.htwdd.htwdresden.types.semsterPlan.Semester} mit formatierten Inhalt
     * @throws ParseException Fehler beim Parsen der Datumsangaben
     * @throws JSONException  Fehler beim Zugriff auf die JSON-Daten
     */
    @NonNull
    public static JSONArray convertSemesterPlanJsonObject(@NonNull final JSONArray semesterPlan) throws JSONException, ParseException {
        final int countSemesterInPlan = semesterPlan.length();
        JSONObject semester;
        JSONArray freeDays;
        int countFreeDays;
        for (int i = 0; i < countSemesterInPlan; i++) {
            semester = semesterPlan.getJSONObject(i);
            semester.put("period", convertTimePeriod(semester.getJSONObject("period")));
            semester.put("lecturePeriod", convertTimePeriod(semester.getJSONObject("lecturePeriod")));
            semester.put("examsPeriod", convertTimePeriod(semester.getJSONObject("examsPeriod")));
            semester.put("reregistration", convertTimePeriod(semester.getJSONObject("reregistration")));
            freeDays = semester.getJSONArray("freeDays");
            countFreeDays = freeDays.length();
            for (int j = 0; j < countFreeDays; j++) {
                freeDays.put(j, convertTimePeriod(freeDays.getJSONObject(j)));
            }
            semester.put("freeDays", freeDays);
            semesterPlan.put(i, semester);

        }
        return semesterPlan;
    }

    /**
     * Formatiert die Daten eines {@link JSONObject} vom Typ {@link de.htwdd.htwdresden.types.semsterPlan.TimePeriod} in ein Format zum Speichern um
     *
     * @param timePeriod JSON-Object welches {@link de.htwdd.htwdresden.types.semsterPlan.TimePeriod} entspricht
     * @return {@link JSONObject} mit formatierten Datumsangaben
     * @throws ParseException Fehler beim Parsen der Datumsangaben
     * @throws JSONException  Fehler beim Zugriff auf die JSON-Daten
     */
    private static JSONObject convertTimePeriod(@NonNull final JSONObject timePeriod) throws ParseException, JSONException {
        final DateFormat dateFormatSrc = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
        final DateFormat dateFormatDest = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.GERMANY);
        timePeriod.put("beginDay", dateFormatDest.format(dateFormatSrc.parse(timePeriod.optString("beginDay", ""))));
        timePeriod.put("endDay", dateFormatDest.format(dateFormatSrc.parse(timePeriod.optString("endDay", ""))));
        return timePeriod;
    }
}
