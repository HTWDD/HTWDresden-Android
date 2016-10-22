package de.htwdd.htwdresden;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import de.htwdd.htwdresden.classes.Const;


public class WizardStgSettingsFragment extends Fragment {

    public WizardStgSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.wizard_second_stg_settings, container, false);

        final TextInputEditText studienJahrgang = (TextInputEditText) view.findViewById(R.id.pref_StgJhr);
        final TextInputEditText studiengang = (TextInputEditText) view.findViewById(R.id.pref_Stg);
        final TextInputEditText studiengruppe = (TextInputEditText) view.findViewById(R.id.pref_StgGrp);
        final Spinner spinnerAbschluss = (Spinner) view.findViewById(R.id.wizard_abschluss);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        studienJahrgang.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENJAHRGANG, ""));
        studiengang.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENGANG, ""));
        studiengruppe.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENGRUPPE, ""));
        final String abschluss = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENABSCHLUSS, "");
        if (!abschluss.isEmpty()) {
            final String[] stringArray = getResources().getStringArray(R.array.abschluss_values);
            for (int i = 0; i < stringArray.length; i++) {
                if (stringArray[i].equals(abschluss)) {
                    spinnerAbschluss.setSelection(i);
                    break;
                }
            }
        }
        return view;
    }
}
