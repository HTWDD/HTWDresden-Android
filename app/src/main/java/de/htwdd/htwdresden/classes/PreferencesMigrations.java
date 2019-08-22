package de.htwdd.htwdresden.classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import de.htwdd.htwdresden.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Klasse um verschiedene Einstellungen zu migrieren
 *
 * @author Kay Förster
 */
public class PreferencesMigrations {
    private final static int newVersion = 2;
    private final Context context;
    private final SharedPreferences sharedPreferences;

    public PreferencesMigrations(@NonNull final Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public void migrate() {
        int oldVersion = sharedPreferences.getInt("version", 0);
        if (oldVersion >= newVersion) {
            return;
        }
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        // Setze Einstellungen beim ersten Starten der App
        if (sharedPreferences.getBoolean("FIRST_RUN", true)) {
            editor.putBoolean("firebase_analytics.enable", false);
            editor.putBoolean("firebase_crashlytics.enable", false);
        }

        // Erste Migration
        if (oldVersion == 0) {
            if (sharedPreferences.contains("StgJhr")) {
                editor.putInt(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR, Integer.parseInt(sharedPreferences.getString("StgJhr", "17")));
            }
            createNotificationChannel();
//            oldVersion++;
        }

        editor.putInt("version", 1);
        editor.apply();
    }

    /**
     * Erstellt Notification Channel
     */
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            return;
        }
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        final NotificationChannel notificationChannel = new NotificationChannel(
                Const.NOTIFICATION_CHANNEL_EXAMS,
                context.getString(R.string.settings_exam_results),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel);
    }
}
