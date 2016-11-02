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

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.IWizardSaveSettings;

/**
 * Wizard Fragment für die Notenabfrage
 *
 * @author Kay Förster
 */
public class WizardUserDataSettingsFragment extends Fragment implements IWizardSaveSettings {
    private TextInputEditText s_nummer;
    private TextInputEditText rzPasswort;

    public WizardUserDataSettingsFragment() {
        // Required empty public constructor
    }

    public static WizardUserDataSettingsFragment newInstance(@NonNull final Bundle bundle) {
        final WizardUserDataSettingsFragment fragment = new WizardUserDataSettingsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.wizard_third_user_data_settings, container, false);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        s_nummer = (TextInputEditText) view.findViewById(R.id.pref_bibNummer);
        rzPasswort = (TextInputEditText) view.findViewById(R.id.pref_Stg);
        s_nummer.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_S_NUMMER, ""));
        rzPasswort.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_RZ_LOGIN, ""));
        return view;
    }

    @Override
    public void onPause() {
        saveSettings(getArguments());
        super.onPause();
    }

    @Override
    public void saveSettings(@NonNull final Bundle bundle) {
        Log.d("UserData 2", "saveSettings");
        bundle.putString(Const.preferencesKey.PREFERENCES_S_NUMMER, s_nummer.getText().toString());
        bundle.putString(Const.preferencesKey.PREFERENCES_RZ_LOGIN, rzPasswort.getText().toString());
        Log.d("UserData 2", s_nummer.getText().toString());
    }
}
