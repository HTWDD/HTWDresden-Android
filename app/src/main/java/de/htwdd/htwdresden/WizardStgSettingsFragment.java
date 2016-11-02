package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.IWizardSaveSettings;

/**
 * Wizard Fragment für den Stunden- / Prüfungsplan
 *
 * @author Kay Förster
 */
public class WizardStgSettingsFragment extends Fragment implements IWizardSaveSettings {
    private String[] abschlussValues;
    private TextInputEditText studienJahrgang;
    private TextInputEditText studiengang;
    private TextInputEditText studiengruppe;
    private Spinner spinnerAbschluss;

    public WizardStgSettingsFragment() {
        // Required empty public constructor
    }

    public static WizardStgSettingsFragment newInstance(@NonNull final Bundle bundle) {
        WizardStgSettingsFragment fragment = new WizardStgSettingsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.wizard_second_stg_settings, container, false);
        abschlussValues = getResources().getStringArray(R.array.abschluss_values);

        studienJahrgang = (TextInputEditText) view.findViewById(R.id.pref_StgJhr);
        studiengang = (TextInputEditText) view.findViewById(R.id.pref_Stg);
        studiengruppe = (TextInputEditText) view.findViewById(R.id.pref_StgGrp);
        spinnerAbschluss = (Spinner) view.findViewById(R.id.wizard_abschluss);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        studienJahrgang.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENJAHRGANG, ""));
        studiengang.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENGANG, ""));
        studiengruppe.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENGRUPPE, ""));
        final String abschluss = sharedPreferences.getString(Const.preferencesKey.PREFERENCES_STUDIENABSCHLUSS, "");
        if (!abschluss.isEmpty()) {
            for (int i = 0; i < abschlussValues.length; i++) {
                if (abschlussValues[i].equals(abschluss)) {
                    spinnerAbschluss.setSelection(i);
                    break;
                }
            }
        }
        return view;
    }

    @Override
    public void onPause() {
        saveSettings(getArguments());
        super.onPause();
    }

    @Override
    public void saveSettings(@NonNull final Bundle bundle) {
        Log.d("UserData 1", "saveSettings");
        bundle.putString(Const.preferencesKey.PREFERENCES_STUDIENJAHRGANG, studienJahrgang.getText().toString());
        bundle.putString(Const.preferencesKey.PREFERENCES_STUDIENGANG, studiengang.getText().toString());
        bundle.putString(Const.preferencesKey.PREFERENCES_STUDIENGRUPPE, studiengruppe.getText().toString());
        bundle.putString(Const.preferencesKey.PREFERENCES_STUDIENABSCHLUSS, abschlussValues[spinnerAbschluss.getSelectedItemPosition()]);
    }
}
