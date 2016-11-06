package de.htwdd.htwdresden;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Wizard Abschlussfragment
 *
 * @author Kay Förster
 */
public class WizardFinalStateFragment extends Fragment {

    public WizardFinalStateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.wizard_fouth_final, container, false);

        final Button button = (Button) view.findViewById(R.id.wizard_finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finishActivity(Activity.RESULT_OK);
            }
        });

        return view;
    }
}

