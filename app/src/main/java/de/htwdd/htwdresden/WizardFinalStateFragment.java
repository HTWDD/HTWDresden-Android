package de.htwdd.htwdresden;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.htwdd.htwdresden.interfaces.IWizardSaveSettings;
import de.htwdd.htwdresden.types.dataBinding.WizardDataBindingObject;


/**
 * Wizard Abschlussfragment
 *
 * @author Kay Förster
 */
public class WizardFinalStateFragment extends Fragment implements IWizardSaveSettings {
    private static WizardDataBindingObject dataBindingObject = null;

    public WizardFinalStateFragment() {
        // Required empty public constructor
    }

    public static WizardFinalStateFragment newInstance(@NonNull final WizardDataBindingObject dataBindingObject) {
        final WizardFinalStateFragment fragment = new WizardFinalStateFragment();
        fragment.setDataBindingObject(dataBindingObject);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.wizard_fouth_final, container, false);

        final Button button = (Button) view.findViewById(R.id.wizard_finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.d("FinalState", "Klick");
                final Activity activity = getActivity();
                dataBindingObject.saveSettings(activity);
                activity.finish();
                Log.d("FinalState", "Finish");
            }
        });

        return view;
    }

    @Override
    public void setDataBindingObject(@NonNull final WizardDataBindingObject dataBindingObject) {
        WizardFinalStateFragment.dataBindingObject = dataBindingObject;
    }
}

