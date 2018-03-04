package de.htwdd.htwdresden.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.API.ITimetableService;
import de.htwdd.htwdresden.classes.API.Retrofit2Rubu;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.LessonUser;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

/**
 * Service zum Aktualisieren des Stundenplans für Studenten
 *
 * @author Kay Förster
 */
public class TimetableStudentSyncService extends AbstractSyncHelper {
    protected final static String LOG_TAG = "TimetableSyncService";
    protected final Stack<LessonUser> results = new Stack<>();

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
        final ITimetableService iTimetableService = Retrofit2Rubu.getInstance(context).getRetrofit().create(ITimetableService.class);
        final Call<List<LessonUser>> lessons = iTimetableService.getStudentTimetable(studienjahr, studiengang, studiengruppe);
        lessons.enqueue(new GenericCallback<List<LessonUser>>() {
            @Override
            void onSuccess(final List<LessonUser> response) {
                results.addAll(response);
                queueCount.decrementCountQueue();
            }
        });
        queueCount.incrementCountQueue();

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
        // Benachrichtigung senden
        if (broadcastNotifier != null) {
            broadcastNotifier.notifyStatus(errorCode, errorMessage);
        }
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
            // einzelne Lehrveranstaltungen durchgehen und überprüfen ob diese gespeichert werde sollen
            String id;
            LessonUser lesson;
            while (!this.results.empty()) {
                lesson = this.results.pop();

                // Überprüfe ob Lehrveranstaltung übersprungen werden kann
                id = lesson.getId();
                if (stateDatabase.containsKey(id) && stateDatabase.get(id).equals(lesson.getLastChanged())) {
                    Log.d(LOG_TAG, "Überspringe Lehrveranstaltung: " + id);
                } else {
                    // Lehrveranstaltung speichern
                    realm.copyToRealmOrUpdate(lesson);
                }
                stateDatabase.remove(id);
            }

            // Lösche alle übrig gebliebenen Stunden
            for (final Map.Entry<String, Date> entry : stateDatabase.entrySet()) {
                realm.where(LessonUser.class).equalTo(Const.database.Lesson.ID, entry.getKey()).findAll().deleteAllFromRealm();
            }
            // Update abschließen
            realm.commitTransaction();
            return true;

        } catch (final Exception e) {
            realm.cancelTransaction();
            Log.e(LOG_TAG, "[Fehler] beim Speichern des Stundenplans", e);
            setError(getString(R.string.timetable_save_error), -1);
            return false;
        } finally {
            realm.close();
        }
    }
}
