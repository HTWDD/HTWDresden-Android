package de.htwdd.htwdresden.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.ExamsHelper;

/**
 * Service zum automatischen aktualisieren von Prüfungsergebnissen und senden einer Benachrichtigung
 *
 * @author Kay Förster
 */
public final class ExamAutoUpdateService extends ExamSyncService {
    public static final int REQUEST_CODE = 9988;
    private final static String LOG = "ExamAutoUpdateService";

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sNummer = sharedPreferences.getString("sNummer", "");
        rzLogin = sharedPreferences.getString("RZLogin", "");

        Log.d(LOG, "Führe Service aus");
//        // Alle Noten laden
//        getGradeResults();
//        // Auf fertigstellung warten
//        waitForFinish();
//        // Ergebnisse speichern
//        if (!isCancel()) {
//            final boolean result = saveGrades();
//            if (result) {
//                broadcastNotifier.notifyStatus(0);

//            }
//        }
    }

    /**
     * Aktiviert die automatische Noten Aktualisierung
     *
     * @param context  aktueller App-Context
     * @param interval Interval in welchem die Noten aktualisiert werden, ideal vom Typ {@link AlarmManager}
     */
    public static void startAutoUpdate(@NonNull final Context context, final long interval) {
        // Construct an intent that will execute the AlarmReceiver
        final Intent intent = new Intent(context.getApplicationContext(), ExamAutoUpdateService.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getService(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pIntent);
    }

    /**
     * Deaktiviert die automatische Noten Aktualisierung
     *
     * @param context aktueller App-Context
     */
    public static void cancelAutoUpdate(@NonNull final Context context) {
        final Intent intent = new Intent(context.getApplicationContext(), ExamAutoUpdateService.class);
        final PendingIntent pIntent = PendingIntent.getService(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    /**
     * Broadcast Receiver welcher nach dem Booten den Service automatisch wieder aktiviert
     */
    public class UpdateExamsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                final long updateInterval = ExamsHelper.getUpdateInterval(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_AUTO_EXAM_UPDATE, "0"));
                if (updateInterval == 0) {
                    ExamAutoUpdateService.cancelAutoUpdate(context);
                } else ExamAutoUpdateService.startAutoUpdate(context, updateInterval);
            }
        }
    }
}
