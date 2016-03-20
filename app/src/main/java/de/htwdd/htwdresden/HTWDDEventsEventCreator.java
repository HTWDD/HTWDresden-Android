package de.htwdd.htwdresden;

import android.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

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

        //((MainActivity) getActivity()).getSupportActionBar().hide();
        //listener.hideToolbar();
        //blabllbalbalblablalblalblalbla
        return view;
    }
/*
    private static OnEventListener listener;

    public interface OnEventListener {
        void hideToolbar() ;
    }

    public static void setOnEventListener(OnEventListener newListener) {
        listener = newListener;
    }
*/


}
