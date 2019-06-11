package de.htwdd.htwdresden.service;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.API.ITimetableService;
import de.htwdd.htwdresden.classes.API.Retrofit2Rubu;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.LessonRoom;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

/**
 * Service zum Aktualisieren des Belegungsplanes
 *
 * @author Kay Förster
 */
public class TimetableRoomSyncService extends AbstractSyncHelper {
    private final static String LOG_TAG = "RTimetableSyncService";
    private final HashMap<String, List<LessonRoom>> results = new HashMap<>();

    public TimetableRoomSyncService() {
        super("RoomTimetableSyncService", Const.IntentParams.BROADCAST_FINISH_TIMETABLE_UPDATE);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        if (intent != null && intent.hasExtra(Const.BundleParams.ROOM_TIMETABLE_ROOM)) {
            getTimetableFromWeb(intent.getStringExtra(Const.BundleParams.ROOM_TIMETABLE_ROOM));
        } else {
            final Realm realm = Realm.getDefaultInstance();
            final RealmResults<LessonRoom> rooms = realm.where(LessonRoom.class).distinct(Const.database.LessonRoom.ROOM).findAll();
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

    /**
     * Lädt den Raumplan vom Webservice herunter und speichert des Response in {@link #results}
     *
     * @param roomName Raumbezeichnung
     */
    private void getTimetableFromWeb(@NonNull final String roomName) {
        final ITimetableService iTimetableService = Retrofit2Rubu.getInstance(context).getRetrofit().create(ITimetableService.class);
        final Call<List<LessonRoom>> lessons = iTimetableService.getRoomTimetable(roomName);
        lessons.enqueue(new GenericCallback<List<LessonRoom>>() {
            @Override
            void onSuccess(final List<LessonRoom> response) {
                results.put(roomName, response);
                queueCount.decrementCountQueue();
            }
        });
        queueCount.incrementCountQueue();
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
            String room;
            List<LessonRoom> lessonRooms;
            for (Map.Entry<String, List<LessonRoom>> arrayEntry : results.entrySet()) {
                room = arrayEntry.getKey();
                lessonRooms = arrayEntry.getValue();

                // Lösche alte Einträge
                realm.where(LessonRoom.class).equalTo(Const.database.LessonRoom.ROOM, room).findAll().deleteAllFromRealm();
                // Raum zuordnen
                for (final LessonRoom lessonRoom : lessonRooms) {
                    lessonRoom.setRoom(room);
                }
                // Einträge speichern
                realm.copyToRealmOrUpdate(lessonRooms);
            }
            // Update abschließen
            realm.commitTransaction();
            return true;
        } catch (final Exception e) {
            realm.cancelTransaction();
            Log.e(LOG_TAG, "[Fehler] Fehler beim Speichern des Raumplanes", e);
            setError(getString(R.string.room_timetable_add_save_error), -1);
            return false;
        } finally {
            realm.close();
        }
    }
}
