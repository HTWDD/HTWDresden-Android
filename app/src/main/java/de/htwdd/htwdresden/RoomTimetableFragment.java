package de.htwdd.htwdresden;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.interfaces.INavigation;


/**
 * Übersicht über alle gespeicherten Räume und ihren Belegungen
 *
 * @author Kay Förster
 */
public class RoomTimetableFragment extends Fragment {


    public RoomTimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Erst Abmelden wenn Fragment entgültig zerstört wird, da ansonsten Nachrichten aus anderen
        // Activitys nicht registriert werden.
        EventBus.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_timetable, container, false);

        // Setze Toolbartitle
        ((INavigation) getActivity()).setTitle(getResources().getString(R.string.navi_room_timetable));

        // Inflate the layout for this fragment
        return view;
    }

}
