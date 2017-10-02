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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.internet.JsonArrayRequestWithBasicAuth;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;

/**
 * Service zum Aktualisieren des Stundenplans für Professoren
 *
 * @author Kay Förster
 */
public class TimetableProfessorSyncService extends TimetableStudentSyncService {

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        Log.d(LOG_TAG, "Starte TimetableProfessorSyncService");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String professorKey = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_PROFESSOR, "");

        // Stundenplan vom Webservice laden
        getTimetableFromWeb(professorKey);
        // Auf fertigstellung warten
        waitForFinish();
        if (!isCancel()) {
            final boolean result = saveTimetable();
            Log.d(LOG_TAG, "Speichern beendet: " + result);
            if (result && broadcastNotifier != null) {
                broadcastNotifier.notifyStatus(0);
            }
        }
    }

    /**
     * Lädt den Stundenplan vom Webservice herunter und speichert des Response in {@link #results}
     *
     * @param professorKey Key zur Identifikation eines Professors
     */
    private void getTimetableFromWeb(@NonNull final String professorKey) {
        String url;
        try {
            url = Const.internet.WEBSERVICE_URL_APP + "v0/professorTimetable.php?key=" + URLEncoder.encode(professorKey, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "[Fehler] Encoding des Professors-Key fehlgeschlagen", e);
            url = Const.internet.WEBSERVICE_URL_APP + "v0/professorTimetable.php?key=" + professorKey;
        }

        final Response.Listener<JSONArray> response = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                results.push(response);
                queueCount.decrementCountQueue();
            }
        };
        final JsonArrayRequestWithBasicAuth request = new JsonArrayRequestWithBasicAuth(
                Request.Method.GET,
                url,
                null,
                response,
                errorListener
        );

        // Request markieren und absenden
        request.setTag(Const.internet.TAG_TIMETABLE);
        queueCount.incrementCountQueue();
        VolleyDownloader.getInstance(context).addToRequestQueue(request);
    }
}
