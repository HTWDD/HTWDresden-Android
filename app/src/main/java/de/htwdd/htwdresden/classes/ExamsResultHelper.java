package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.htwdd.htwdresden.QueueCount;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.ExamResultDAO;
import de.htwdd.htwdresden.types.ExamResult;

/**
 * Bündelt alle wesentlichen Methoden zum Abfragen der Noten in einer Klasse
 *
 * @author Kay Förster
 */
public class ExamsResultHelper {
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final QueueCount queueCount;
    private final ArrayList<ExamResult> examResults = new ArrayList<>();

    public ExamsResultHelper(@NonNull Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.queueCount = new QueueCount();
    }

    public QueueCount getQueueCount() {
        return queueCount;
    }

    public ArrayList<ExamResult> getExamResults() {
        return examResults;
    }

    /**
     * Prüft ob zur Notenabfrage alle Einstellunen gesetzt sind.
     *
     * @return true wenn alle Einstellungen gesetzt sind, sonst false
     */
    public boolean checkPreferences() {
        return !(sharedPreferences.getString("sNummer", "").length() < 5 || sharedPreferences.getString("RZLogin", "").length() < 3);
    }

    /**
     * Führt ein Request zum Anfordern der verfügbaren Studiengänge eines Studenten aus
     *
     * @param getcoursesListener Callback-Interface für das Ergebniss der Anfrage
     * @param errorListener      Callback-Interface zum behandeln von Fehlern
     */
    public void makeCoursesRequest(@Nullable Response.Listener<JSONArray> getcoursesListener, @Nullable Response.ErrorListener errorListener) {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.POST,
                Const.internet.WEBSERVICE_URL_HISQIS + "getcourses" +
                        "?sNummer=s" + sharedPreferences.getString("sNummer", "") +
                        "&RZLogin=" + Uri.encode(sharedPreferences.getString("RZLogin", "")),
                getcoursesListener,
                errorListener);
        arrayRequest.setTag(Const.internet.TAG_EXAM_RESULTS);
        VolleyDownloader.getInstance(context).addToRequestQueue(arrayRequest);
    }

    /**
     * Startet für jeden Studiengang des Studenten eine Abfrage der Noten
     *
     * @param response      Ergebnis von #{makeCoursesRequest}
     * @param arrayListener Callback-Interface für das Ergebniss der Anfrage
     * @param errorListener Callback-Interface zum behandeln von Fehlern
     * @throws JSONException
     */
    public void makeGradeRequests(@NonNull final JSONArray response, @Nullable Response.Listener<JSONArray> arrayListener, @Nullable Response.ErrorListener errorListener) throws JSONException {
        int count = response.length();
        // Einzelne Studiengänge durchgehen
        for (int i = 0; i < count; i++) {
            JSONObject jsonObject = response.getJSONObject(i);

            String url = Const.internet.WEBSERVICE_URL_HISQIS + "getgrades" +
                    "?sNummer=s" + sharedPreferences.getString("sNummer", "") +
                    "&RZLogin=" + Uri.encode(sharedPreferences.getString("RZLogin", "")) +
                    "&AbschlNr=" + jsonObject.getString("AbschlNr") +
                    "&StgNr=" + jsonObject.getString("StgNr") +
                    "&POVersion=" + jsonObject.getString("POVersion");

            // Noten für den entsprechenden Studiengang laden
            JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.POST, url, arrayListener, errorListener);
            arrayRequest.setTag(Const.internet.TAG_EXAM_RESULTS);
            VolleyDownloader.getInstance(context).addToRequestQueue(arrayRequest);
            queueCount.countQueue++;
        }
    }

    /**
     * Wandelt das übergebene JSON-Array in eine Liste von Objekten  #{ExamResult} um
     *
     * @param response Ergebnis von #{makeGradeRequests}
     * @throws JSONException
     */
    public void getGradesListener(@NonNull JSONArray response) throws JSONException {
        // Ergebnisse parsen
        int count = response.length();
        for (int i = 0; i < count; i++) {
            ExamResult examResult = new ExamResult();
            examResult.parseFromJSON(response.getJSONObject(i));
            examResults.add(examResult);
        }
        queueCount.countQueue--;
    }

    /**
     * Soeichert alle Noten aus {#examResults} in die Datenbank
     *
     * @return false im Fehlerfall, sonst true
     */
    public boolean saveExamResults() {
        ExamResultDAO dao = new ExamResultDAO(new DatabaseManager(context));
        return dao.replaceExamResults(examResults);
    }
}
