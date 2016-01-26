package de.htwdd.htwdresden;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import de.htwdd.htwdresden.adapter.TimetableListAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.Lesson;


public class TimetableDetailsFragment extends Fragment {
    private ArrayList<Lesson> lessons;

    public TimetableDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lessons = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetable_details, container, false);

        final Bundle bundle = getArguments();
        int ds = bundle.getInt(Const.BundleParams.TIMETABLE_DS, 1);
        int day = bundle.getInt(Const.BundleParams.TIMETABLE_DAY, 1);
        int week = bundle.getInt(Const.BundleParams.TIMETABLE_WEEK, 1);

        // Ändere Titel
        if (getActivity() instanceof INavigation && !bundle.getBoolean(Const.BundleParams.TIMETABLE_EDIT, false))
            ((INavigation) getActivity()).setTitle(getResources().getString(R.string.timetable_details));

        // Lade Stunden aus DB
        DatabaseManager databaseManager = new DatabaseManager(getActivity());
        TimetableUserDAO timetableUserDAO = new TimetableUserDAO(databaseManager);
        lessons.addAll(timetableUserDAO.getByDS(week, day, ds));

        // Adapter für Daten erstellen
        TimetableListAdapter timetableListAdapter = new TimetableListAdapter(getActivity(), lessons);

        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(timetableListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle1 = new Bundle();
                bundle1.putLong(Const.BundleParams.TIMETABLE_LESSON_ID, lessons.get(i).getId());
                bundle1.putBoolean(Const.BundleParams.TIMETABLE_EDIT, true);

                Fragment fragment = new TimetableEditFragment();
                fragment.setArguments(bundle1);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.activity_sync_FrameLayout, fragment).commit();
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putBoolean(Const.BundleParams.TIMETABLE_CREATE, true);

                Fragment fragment = new TimetableEditFragment();
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.activity_sync_FrameLayout, fragment).commit();
            }
        });

        return view;
    }
}
