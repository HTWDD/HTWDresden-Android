package de.htwdd.htwdresden;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.adapter.TimetableRoomGridAdapter;
import de.htwdd.htwdresden.classes.Const;
import io.realm.Realm;


public class RoomTimetableOverviewFragment extends Fragment {
    private Realm realm;

    public RoomTimetableOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mLayout = inflater.inflate(R.layout.fragment_timetable_overview, container, false);
        realm = Realm.getDefaultInstance();

        // Arguments überprüfen
        final Bundle bundle = getArguments();
        if (bundle == null) {
            return mLayout;
        }

        final int week = bundle.getInt(Const.BundleParams.TIMETABLE_WEEK, new GregorianCalendar(Locale.GERMANY).get(Calendar.WEEK_OF_YEAR));
        final String room = bundle.getString(Const.BundleParams.ROOM_TIMETABLE_ROOM, "");

        // SwipeRefreshLayout deaktivieren
        mLayout.findViewById(R.id.swipeRefreshLayout).setEnabled(false);

        // GridView
        ((GridView) mLayout.findViewById(R.id.timetable)).setAdapter(new TimetableRoomGridAdapter(realm, room, week, true));

        return mLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}
