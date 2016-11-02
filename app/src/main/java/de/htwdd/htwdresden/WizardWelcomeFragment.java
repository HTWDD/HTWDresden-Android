package de.htwdd.htwdresden;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class WizardWelcomeFragment extends Fragment {

    public WizardWelcomeFragment() {
    }

    public static WizardWelcomeFragment newInstance() {
        return new WizardWelcomeFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wizard_first_welcome, container, false);
    }
}
