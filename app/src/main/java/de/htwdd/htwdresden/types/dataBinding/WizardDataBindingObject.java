package de.htwdd.htwdresden.types.dataBinding;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;

/**
 * Data Binding-Objekt für Wizard, welches Daten automatisch lädt und speichert
 *
 * @author Kay Förster
 */
public class WizardDataBindingObject {
    private final String[] studyDegreeValues;
    // Einstellungen für Stunden-/Prüfungsplan
    public int studyGroupYear;
    public String studyGroupCurse;
    public String studyGroup;
    public int studyDegree;
    // Einstellungen für Notenabfrage
    public String s_nummer;
    public String rzPasswort;

    public WizardDataBindingObject(@NonNull final Context context) {
        // Lade Settings aus den SharedPreferences
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        s_nummer = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_S_NUMMER, "");
        rzPasswort = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_RZ_LOGIN, "");
        studyGroupYear = Integer.valueOf(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENJAHRGANG, "0"));
        studyGroupCurse = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENGANG, "");
        studyGroup = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENGRUPPE, "");
        final String studyDegree = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENABSCHLUSS, "");
        studyDegreeValues = context.getResources().getStringArray(R.array.abschluss_values);
        for (int i = 0; i < studyDegreeValues.length; i++) {
            if (studyDegreeValues[i].equals(studyDegree)) {
                this.studyDegree = i;
                break;
            }
        }
    }

    /**
     * Speichert die vorgenommen Änderungen in den Einstellungen
     *
     * @param context aktueller App-Context
     */
    public void saveSettings(@NonNull final Context context) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(Const.preferencesKey.PREFERENCES_S_NUMMER, s_nummer);
        Log.d("Speichere", "sNummer: " + s_nummer);
        Log.d("Speichere", "selectedItemPosition: " + studyDegree);
        editor.putString(Const.preferencesKey.PREFERENCES_RZ_LOGIN, rzPasswort);
        editor.putString(Const.preferencesKey.PREFERENCES_STUDIENABSCHLUSS, studyDegreeValues[studyDegree]);
        editor.putString(Const.preferencesKey.PREFERENCES_STUDIENJAHRGANG, String.valueOf(studyGroupYear));
        editor.putString(Const.preferencesKey.PREFERENCES_STUDIENGANG, studyGroupCurse);
        editor.putString(Const.preferencesKey.PREFERENCES_STUDIENGRUPPE, studyGroup);
        editor.apply();
    }
}
