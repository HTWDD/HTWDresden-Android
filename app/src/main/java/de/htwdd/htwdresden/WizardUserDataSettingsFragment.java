package de.htwdd.htwdresden;


import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.htwdd.htwdresden.databinding.WizardThirdUserDataSettingsBinding;
import de.htwdd.htwdresden.interfaces.IWizardSaveSettings;
import de.htwdd.htwdresden.types.dataBinding.WizardDataBindingObject;

/**
 * Wizard Fragment für die Notenabfrage
 *
 * @author Kay Förster
 */
public class WizardUserDataSettingsFragment extends Fragment implements IWizardSaveSettings {
    private static WizardDataBindingObject dataBindingObject = null;

    public WizardUserDataSettingsFragment() {
        // Required empty public constructor
    }

    public static WizardUserDataSettingsFragment newInstance(@NonNull final WizardDataBindingObject dataBindingObject) {
        final WizardUserDataSettingsFragment fragment = new WizardUserDataSettingsFragment();
        fragment.setDataBindingObject(dataBindingObject);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final WizardThirdUserDataSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.wizard_third_user_data_settings, container, false);
        binding.setSettings(dataBindingObject);

        return binding.getRoot();
    }

    @Override
    public void setDataBindingObject(@NonNull final WizardDataBindingObject dataBindingObject) {
        WizardUserDataSettingsFragment.dataBindingObject = dataBindingObject;
    }
}
