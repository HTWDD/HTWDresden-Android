package de.htwdd.htwdresden;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


// TODO: 25.05.16 read about viewpager static classes
/**
 * A placeholder fragment containing a simple view.
 */
public class WizardWelcomeFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public WizardWelcomeFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WizardWelcomeFragment newInstance(int sectionNumber) {
        WizardWelcomeFragment fragment = new WizardWelcomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: 24.05.16 right fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wizard_first_welcome, container, false);
        return rootView;
    }
}
