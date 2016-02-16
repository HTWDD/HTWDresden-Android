package de.htwdd.htwdresden;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.htwdd.htwdresden.adapter.TimetableGridAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableRoomDAO;
import de.htwdd.htwdresden.types.Lesson;


public class RoomTimetableOverviewFragment extends Fragment {

    public RoomTimetableOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mLayout = inflater.inflate(R.layout.fragment_timetable_overview, container, false);

        // Arguments überprüfen
        Bundle bundle = getArguments();
        int week;
        String room;
        if (bundle != null) {
            week = bundle.getInt(Const.BundleParams.TIMETABLE_WEEK, new GregorianCalendar().get(Calendar.WEEK_OF_YEAR));
            room = bundle.getString(Const.BundleParams.ROOM_TIMETABLE_ROOM, "");
        } else return mLayout;

        // SwipeRefreshLayout deaktivieren
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);

        // Lade Daten aus DB
        DatabaseManager databaseManager = new DatabaseManager(getActivity());
        TimetableRoomDAO timetableUserDAO = new TimetableRoomDAO(databaseManager);
        ArrayList<Lesson> lessons_week = new ArrayList<>();
        lessons_week.addAll(timetableUserDAO.getWeekShort(week, room));

        // Adapter zum handeln der Daten
        TimetableGridAdapter gridAdapter = new TimetableGridAdapter(getActivity(), lessons_week, week);

        // GridView
        GridView gridView = (GridView) mLayout.findViewById(R.id.timetable);
        gridView.setAdapter(gridAdapter);

        return mLayout;
    }
}
