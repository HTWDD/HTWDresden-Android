package de.htwdd.htwdresden;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.htwdd.htwdresden.types.User;


public class HTWDDEventsSignUp extends Fragment {


    public static final int NICKNAME_LENGTH = 3;
    public static final int FIRSTNAME_LENGTH = 2;
    public static final int LASTNAME_LENGTH = 2;
    public static final int STG_LENGTH = 2;
    public static final String TAG = "signup";

    public HTWDDEventsSignUp() {
    }

    public static HTWDDEventsSignUp newInstance() {
        HTWDDEventsSignUp fragment = new HTWDDEventsSignUp();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_htwddevents_sign_up, container, false);

        //User.checkUserExistAPI(getActivity(), view,);
        User.checkUserExistAPI(
                getActivity(),
                view,
                new User.PostRequestResponseListener() {
                    @Override
                    public void requestStarted() {
                        Toast.makeText(getActivity(), "RequestStarted", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //if(!(pass.gehoertZu(user))) dasPasswortIstFalschMeldung();startWizard(name,pass);

        return view;
    }

    public interface OnFragmentInteractionListener {
        void onSignupFragmentInteraction(String string);
    }
}
