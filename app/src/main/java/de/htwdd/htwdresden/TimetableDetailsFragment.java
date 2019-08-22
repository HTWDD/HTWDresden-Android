package de.htwdd.htwdresden;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.adapter.TimetableListAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.LessonUser;
import io.realm.Realm;
import io.realm.RealmResults;


public class TimetableDetailsFragment extends Fragment {
    private Realm realm;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_timetable_details, container, false);
        final Activity activity = getActivity();
        final Bundle bundle = new Bundle(getArguments());

        // Ändere Titel
        if (activity instanceof INavigation && !bundle.getBoolean(Const.BundleParams.TIMETABLE_EDIT, false)) {
            ((INavigation) activity).setTitle(getResources().getString(R.string.timetable_details));
        }

        // Lade Stunden aus DB
        realm = Realm.getDefaultInstance();
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        calendar.set(Calendar.DAY_OF_WEEK, bundle.getInt(Const.BundleParams.TIMETABLE_DAY, 1) + 1);
        calendar.set(Calendar.WEEK_OF_YEAR, bundle.getInt(Const.BundleParams.TIMETABLE_WEEK, 1));
        final RealmResults<LessonUser> lessons = TimetableHelper.getLessonsByDateAndDs(
                realm,
                calendar,
                bundle.getInt(Const.BundleParams.TIMETABLE_DS, 1),
                bundle.getBoolean(Const.BundleParams.TIMETABLE_FILTER_CURRENT_WEEK, true),
                bundle.getBoolean(Const.BundleParams.TIMETABLE_FILTER_SHOW_HIDDEN, false));

        // Adapter für Daten erstellen
        final TimetableListAdapter timetableListAdapter = new TimetableListAdapter(requireContext(), lessons);

        final ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(timetableListAdapter);
        listView.setOnItemClickListener((adapterView, view12, i, l) -> {
            final Bundle bundle1 = new Bundle();
            bundle1.putString(Const.BundleParams.TIMETABLE_LESSON_ID, lessons.get(i).getId());
            bundle1.putBoolean(Const.BundleParams.TIMETABLE_EDIT, true);

            startEditFragment(bundle1);
        });

        final FloatingActionButton floatingActionButton = view.findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(view1 -> {
            bundle.putBoolean(Const.BundleParams.TIMETABLE_EDIT, false);
            startEditFragment(bundle);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    /**
     * Startet das Bearbeiten-Fragment
     *
     * @param bundle Argumente zur Auswahl einer Stunde
     */
    private void startEditFragment(@NonNull final Bundle bundle) {
        final Fragment fragment = new TimetableEditFragment();
        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_sync_FrameLayout, fragment).commit();
    }
}
