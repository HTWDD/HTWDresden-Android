package de.htwdd.htwdresden.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Stack;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.internet.JsonArrayRequestWithBasicAuth;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.types.ExamResult;
import io.realm.Realm;

/**
 * Service zum Aktualisieren der Prüfungsergebnisse
 *
 * @author Kay Förster
 */
public class ExamSyncService extends AbstractSyncHelper {
    public final static String INTENT_SYNC_EXAMS = "de.htwdd.htwdresden.exams";
    private final static String LOG_TAG = "ExamSyncService";
    private final Stack<JSONArray> results = new Stack<>();
    // Zugangsdaten
    String sNummer;
    String rzLogin;

    public ExamSyncService() {
        super("ExamSyncService", INTENT_SYNC_EXAMS);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sNummer = sharedPreferences.getString("sNummer", "");
        rzLogin = sharedPreferences.getString("RZLogin", "");

        // Alle Noten laden
        getGradeResults();
        // Auf fertigstellung warten
        waitForFinish();
        // Ergebnisse speichern
        if (!isCancel()) {
            final boolean result = saveGrades();
            if (result && broadcastNotifier != null) {
                broadcastNotifier.notifyStatus(0);
            }
        }
    }

    @Override
    void setError(@NonNull final String errorMessage) {
        // Synchronisation abbrechen
        setCancelToTrue();
        // Downloads abbrechen
        VolleyDownloader.getInstance(context).getRequestQueue().cancelAll(Const.internet.TAG_EXAM_RESULTS);
        // Benachrichtigung senden
        if (broadcastNotifier != null)
            broadcastNotifier.notifyStatus(-1, errorMessage);
    }

    /**
     * Fordert alle Noten eines Studenten vom Webservice an
     */
    void getGradeResults() {
        final Response.Listener<JSONArray> response = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                JSONObject jsonObject;
                final int responseCount = response.length();
                try {
                    for (int i = 0; i < responseCount; i++) {
                        jsonObject = response.getJSONObject(i);
                        getGrades(jsonObject.getString("AbschlNr"), jsonObject.getString("StgNr"), jsonObject.getString("POVersion"));
                    }
                } catch (final JSONException e) {
                    Log.e(LOG_TAG, "[Fehler] Beim Verarbeiten der verfügbaren Studiengänge", e);
                    setError("Fehler beim Verarbeiten der verfügbaren Studiengänge");
                }
                queueCount.decrementCountQueue();
            }
        };
        final JsonArrayRequestWithBasicAuth request = new JsonArrayRequestWithBasicAuth(
                Request.Method.GET,
                Const.internet.WEBSERVICE_URL_HISQIS + "getcourses",
                null,
                response,
                errorListener
        );
        request.authentifikation("s" + sNummer, rzLogin);

        // Request markieren und absenden
        request.setTag(Const.internet.TAG_EXAM_RESULTS);
        queueCount.incrementCountQueue();
        VolleyDownloader.getInstance(context).addToRequestQueue(request);
    }

    /**
     * Lädt Noten vom Webservice herunter und speichert des Response in {@link #results}
     *
     * @param abschlussNummer    interne Nummer des Abschlusses
     * @param studiengangsnummer interne Nummer des Studienganges
     * @param poVersion          Version der Prüfungsordnung
     */
    private void getGrades(@NonNull final String abschlussNummer, @NonNull final String studiengangsnummer, @NonNull final String poVersion) {
        final Response.Listener<JSONArray> response = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                results.add(response);
                queueCount.decrementCountQueue();
            }
        };
        final String url = Const.internet.WEBSERVICE_URL_HISQIS + "getgrades?AbschlNr=" + abschlussNummer + "&StgNr=" + studiengangsnummer + "&POVersion=" + poVersion;
        final JsonArrayRequestWithBasicAuth request = new JsonArrayRequestWithBasicAuth(Request.Method.GET, url, null, response, errorListener);
        request.authentifikation("s" + sNummer, rzLogin);
        request.setTag(Const.internet.TAG_EXAM_RESULTS);
        queueCount.incrementCountQueue();
        VolleyDownloader.getInstance(context).addToRequestQueue(request);
    }

    /**
     * Speichert alle Noten aus {@link #results} in die Datenbank
     *
     * @return true wenn Speichern erfolgreich
     */
    boolean saveGrades() {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        try {
            realm.delete(ExamResult.class);
            for (final JSONArray jsonArray : results) {
                realm.createAllFromJson(ExamResult.class, jsonArray);
            }
            realm.commitTransaction();
        } catch (final Exception e) {
            Log.e(LOG_TAG, "[Fehler] Beim Speichern der Noten", e);
            realm.cancelTransaction();
            setError(context.getString(R.string.info_error_save));
            return false;
        } finally {
            realm.close();
        }
        return true;
    }
}
