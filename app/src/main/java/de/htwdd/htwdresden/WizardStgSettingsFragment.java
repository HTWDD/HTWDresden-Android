package de.htwdd.htwdresden;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class WizardStgSettingsFragment extends Fragment {

    View mView;
    Activity mActivity;
    public WizardStgSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.wizard_second_stg_settings, container, false);
        mActivity = getActivity();

        return mView;
    }


}
