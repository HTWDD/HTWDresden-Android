package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Hilfsklasse für Noten
 */
public final class ExamsHelper {

    public static boolean checkPreferences(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return !(sharedPreferences.getString("sNummer", "").length() < 5 || sharedPreferences.getString("RZLogin", "").length() < 3);
    }
}
