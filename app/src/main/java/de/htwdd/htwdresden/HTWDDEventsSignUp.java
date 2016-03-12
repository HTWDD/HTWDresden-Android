package de.htwdd.htwdresden;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HTWDDEventsSignUp extends Fragment {


    public HTWDDEventsSignUp() {}

    public static HTWDDEventsSignUp newInstance() {
        HTWDDEventsSignUp fragment = new HTWDDEventsSignUp();
        return fragment;
    }
    static public void isUserRegestriert(View view){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_htwddevents_sign_up, container, false);

        return view;
    }

}
