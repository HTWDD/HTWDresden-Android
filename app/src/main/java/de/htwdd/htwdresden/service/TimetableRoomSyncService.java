package de.htwdd.htwdresden.service;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.TimetableRoomHelper;
import de.htwdd.htwdresden.classes.internet.JsonArrayRequestWithBasicAuth;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.types.LessonRoom;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Service zum Aktualisieren des Belegungsplanes
 *
 * @author Kay Förster
 */
public class TimetableRoomSyncService extends AbstractSyncHelper {
    private final static String LOG_TAG = "RTimetableSyncService";
    private final HashMap<String, JSONArray> results = new HashMap<>();

    public TimetableRoomSyncService() {
        super("RoomTimetableSyncService", Const.IntentParams.BROADCAST_FINISH_TIMETABLE_UPDATE);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        if (intent != null && intent.hasExtra(Const.BundleParams.ROOM_TIMETABLE_ROOM)) {
            getTimetableFromWeb(intent.getStringExtra(Const.BundleParams.ROOM_TIMETABLE_ROOM));
        } else {
            final Realm realm = Realm.getDefaultInstance();
            final RealmResults<LessonRoom> rooms = realm.where(LessonRoom.class).distinct(Const.database.LessonRoom.ROOM);
            for (final LessonRoom room : rooms) {
                getTimetableFromWeb(room.getRoom());
            }
            realm.close();
        }

        // Auf Fertigstellung warten
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
     * Lädt den Raumplan vom Webservice herunter und speichert des Response in {@link #results}
     *
     * @param roomName Encodierte Raumbezeichnung
     */
    private void getTimetableFromWeb(@NonNull final String roomName) {
        final Response.Listener<JSONArray> response = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                results.put(roomName.toUpperCase(), response);
                queueCount.decrementCountQueue();
            }
        };

        String encodedRoomName;
        try {
            encodedRoomName = URLEncoder.encode(roomName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(LOG_TAG, "Fehler beim Encoding des Raumes", e);
            encodedRoomName = roomName;
        }
        final JsonArrayRequestWithBasicAuth request = new JsonArrayRequestWithBasicAuth(
                Request.Method.GET,
                Const.internet.WEBSERVICE_URL_APP + "/v0/roomTimetable.php?room=" + encodedRoomName,
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
     * Speichert den Raumplan
     *
     * @return true wenn erfolgreich gespeichert, sonst false
     */
    private boolean saveTimetable() {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        try {
            int countResults;
            String room;
            JSONArray jsonResults;
            JSONObject jsonObject;
            for (Map.Entry<String, JSONArray> arrayEntry : results.entrySet()) {
                room = arrayEntry.getKey();
                jsonResults = arrayEntry.getValue();
                countResults = jsonResults.length();

                // Lösche alte Einträge
                realm.where(LessonRoom.class).equalTo(Const.database.LessonRoom.ROOM, room).findAll().deleteAllFromRealm();

                // Einzelne Lehrveranstaltungen speichern
                for (int i = 0; i < countResults; i++) {
                    jsonObject = TimetableRoomHelper.convertTimetableJsonObject(jsonResults.getJSONObject(i));
                    jsonObject.put(Const.database.LessonRoom.ROOM, room);
                    realm.createOrUpdateObjectFromJson(LessonRoom.class, jsonObject);
                }
            }
            // Update abschließen
            realm.commitTransaction();
            return true;
        } catch (final JSONException e) {
            realm.cancelTransaction();
            Log.e(LOG_TAG, "[Fehler] bei der Verarbeitung des JSON-Responses", e);
            setError(getString(R.string.room_timetable_add_save_error), -1);
            return false;
        } finally {
            realm.close();
        }
    }
}
