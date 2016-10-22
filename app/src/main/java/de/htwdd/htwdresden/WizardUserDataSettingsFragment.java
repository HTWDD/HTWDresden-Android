package de.htwdd.htwdresden;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.htwdd.htwdresden.classes.Const;

public class WizardUserDataSettingsFragment extends Fragment {

    public WizardUserDataSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.wizard_third_user_data_settings, container, false);

        final TextInputEditText s_nummer = (TextInputEditText) view.findViewById(R.id.pref_bibNummer);
        final TextInputEditText rzPasswort = (TextInputEditText) view.findViewById(R.id.pref_Stg);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        s_nummer.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_S_NUMMER, ""));
        rzPasswort.setText(sharedPreferences.getString(Const.preferencesKey.PREFERENCES_RZ_LOGIN, ""));
        return view;
    }
}
