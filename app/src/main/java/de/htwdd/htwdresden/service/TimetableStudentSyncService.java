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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.classes.internet.JsonArrayRequestWithBasicAuth;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.types.LessonUser;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Service zum Aktualisieren des Stundenplans für Studenten
 *
 * @author Kay Förster
 */
public class TimetableStudentSyncService extends AbstractSyncHelper {
    protected final static String LOG_TAG = "TimetableSyncService";
    protected final Stack<JSONArray> results = new Stack<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.GERMANY);

    public TimetableStudentSyncService() {
        super("TimetableSyncService", Const.IntentParams.BROADCAST_FINISH_TIMETABLE_UPDATE);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        Log.d(LOG_TAG, "Starte TimetableStudentSyncService");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final int studienjahr = sharedPreferences.getInt(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR, 18);
        final String studiengang = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGANG, "");
        final String studiengruppe = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGRUPPE, "");

        // Stundenplan vom Webservice laden
        getTimetableFromWeb(studienjahr, studiengang, studiengruppe);
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

    @Override
    void setError(@NonNull final String errorMessage, final int errorCode) {
        // Synchronisation abbrechen
        setCancelToTrue();
        // Downloads abbrechen
        VolleyDownloader.getInstance(context).getRequestQueue().cancelAll(Const.internet.TAG_TIMETABLE);
        // Benachrichtigung senden
        if (broadcastNotifier != null)
            broadcastNotifier.notifyStatus(errorCode, errorMessage);
    }

    /**
     * Lädt den Stundenplan vom Webservice herunter und speichert des Response in {@link #results}
     *
     * @param studienjahr   Jahr der Immatrikulation des Studenten
     * @param studiengang   Studiengang des Studenten
     * @param studiengruppe Studiengruppe des Studenten
     */
    private void getTimetableFromWeb(final int studienjahr, @NonNull final String studiengang, @NonNull final String studiengruppe) {
        final Response.Listener<JSONArray> response = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                results.push(response);
                queueCount.decrementCountQueue();
            }
        };
        final JsonArrayRequestWithBasicAuth request = new JsonArrayRequestWithBasicAuth(
                Request.Method.GET,
                Const.internet.WEBSERVICE_URL_APP + "v0/studentTimetable.php?StgJhr=" + studienjahr + "&Stg=" + studiengang + "&StgGrp=" + studiengruppe,
                null,
                response,
                errorListener
        );

        // Request markieren und absenden
        request.setTag(Const.internet.TAG_TIMETABLE);
        queueCount.incrementCountQueue();
        VolleyDownloader.getInstance(context).addToRequestQueue(request);
    }

    /**
     * Speichert den Stundenplan
     *
     * @return true wenn erfolgreich gespeichert, sonst false
     */
    protected boolean saveTimetable() {
        final Realm realm = Realm.getDefaultInstance();
        final HashMap<String, Date> stateDatabase = new HashMap<>((int) realm.where(LessonUser.class).count());
        final RealmResults<LessonUser> results = realm.where(LessonUser.class).equalTo(Const.database.Lesson.CREATED_BY_USER, false).findAll();
        for (final LessonUser lesson : results) {
            stateDatabase.put(lesson.getId(), lesson.getLastChanged());
        }

        realm.beginTransaction();
        try {
            // Speichere einzelne Results
            String id;
            int countResults;
            LessonUser lesson;
            JSONArray jsonResults;
            JSONObject jsonResult;

            while (!this.results.empty()) {
                jsonResults = this.results.pop();
                countResults = jsonResults.length();

                // einzelne Lehrveranstaltungen durchgehen und überprüfen ob diese gespeichert werde sollen
                for (int i = 0; i < countResults; i++) {
                    jsonResult = TimetableHelper.convertTimetableJsonObject(jsonResults.getJSONObject(i));

                    try {
                        // Überprüfe ob Lehrveranstaltung übersprungen werden kann
                        id = jsonResult.getString("id");
                        if (stateDatabase.containsKey(id)) {
                            if (stateDatabase.get(id).equals(dateFormat.parse(jsonResult.getString("lastChanged")))) {
                                stateDatabase.remove(id);
                                Log.d(LOG_TAG, "Überspringe Lehrveranstaltung: " + id);
                                continue;
                            }
                        }
                    } catch (final ParseException e) {
                        Log.e(LOG_TAG, "[Fehler] Beim Verarbeiten des lastChanged Attributs", e);
                    }

                    lesson = realm.createOrUpdateObjectFromJson(LessonUser.class, jsonResult);
                    stateDatabase.remove(lesson.getId());
                }
            }

            // Lösche alle übrig gebliebenen Stunden
            for (final Map.Entry<String, Date> entry : stateDatabase.entrySet()) {
                realm.where(LessonUser.class).equalTo(Const.database.Lesson.ID, entry.getKey()).findAll().deleteAllFromRealm();
            }
            // Update abschließen
            realm.commitTransaction();
            return true;

        } catch (final JSONException e) {
            realm.cancelTransaction();
            Log.e(LOG_TAG, "[Fehler] bei der Verarbeitung des JSON-Responses", e);
            setError(getString(R.string.timetable_save_error), -1);
            return false;
        } finally {
            realm.close();
        }
    }
}
