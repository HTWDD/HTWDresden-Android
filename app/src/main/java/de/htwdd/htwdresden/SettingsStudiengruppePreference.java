package de.htwdd.htwdresden;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Gruppierter Einstellungsdialog für die Studiengruppe
 *
 * @author Kay Förster
 */
public class SettingsStudiengruppePreference extends DialogPreference {
    private TextView jahrgang;
    private TextView studiengang;
    private TextView studiengruppe;

    public SettingsStudiengruppePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        setDialogLayoutResource(R.layout.preferences_studiengruppe);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        jahrgang = (TextView) view.findViewById(R.id.pref_bibNummer);
        studiengang = (TextView) view.findViewById(R.id.pref_Stg);
        studiengruppe = (TextView) view.findViewById(R.id.pref_StgGrp);

        SharedPreferences sharedPreferences = getSharedPreferences();

        jahrgang.setText(sharedPreferences.getString("StgJhr", ""));
        studiengang.setText(sharedPreferences.getString("Stg", ""));
        studiengruppe.setText(sharedPreferences.getString("StgGrp", ""));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            SharedPreferences.Editor editor = getEditor();
            editor.putString("StgJhr", jahrgang.getText().toString());
            editor.putString("Stg", studiengang.getText().toString());
            editor.putString("StgGrp", studiengruppe.getText().toString());
            editor.commit();
        }
    }
}
