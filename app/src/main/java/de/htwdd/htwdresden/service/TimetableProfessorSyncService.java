package de.htwdd.htwdresden.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import de.htwdd.htwdresden.classes.API.ITimetableService;
import de.htwdd.htwdresden.classes.API.Retrofit2Rubu;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.LessonUser;
import retrofit2.Call;

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
        final ITimetableService iTimetableService = Retrofit2Rubu.getInstance(context).getRetrofit().create(ITimetableService.class);
        final Call<List<LessonUser>> lessons = iTimetableService.getProfessorTimetable(professorKey);
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
}
