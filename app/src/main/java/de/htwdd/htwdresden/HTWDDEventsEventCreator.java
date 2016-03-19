package de.htwdd.htwdresden;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.htwdd.htwdresden.interfaces.INavigation;


public class HTWDDEventsEventCreator extends Fragment {

    public HTWDDEventsEventCreator() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_htwddevents_event_creator, container, false);
        ((INavigation)getActivity()).setTitle("Event erstellen/ändern");

        return view;
    }

}
