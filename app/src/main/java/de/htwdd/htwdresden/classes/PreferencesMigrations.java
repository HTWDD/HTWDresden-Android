package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Klasse um verschiedene Einstellungen zu migrieren
 *
 * @author Kay Förster
 */
public class PreferencesMigrations {
    private final SharedPreferences sharedPreferences;
    private final static int newVersion = 1;

    public PreferencesMigrations(@NonNull final Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void migrate() {
        int oldVersion = sharedPreferences.getInt("version", 0);
        if (oldVersion >= newVersion) {
            return;
        }
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        // Setze Einstellungen beim ersten Starten der App
        if (sharedPreferences.getBoolean("FIRST_RUN", true)) {
            editor.putBoolean("FIRST_RUN", false);
            editor.putBoolean("acra.enable", true);
        }


        // Erste Migration
        if (oldVersion == 0) {
            if (sharedPreferences.contains("StgJhr")) {
                editor.putInt(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR, Integer.parseInt(sharedPreferences.getString("StgJhr", "17")));
            }
//            oldVersion++;
        }

        editor.putInt("version", newVersion);
        editor.apply();
    }
}
