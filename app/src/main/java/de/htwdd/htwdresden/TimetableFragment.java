package de.htwdd.htwdresden;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.htwdd.htwdresden.adapter.ViewPagerAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.TabItem;


/**
 * Hauptfragment welches die verschiedenen Sub-Fragmente enthält
 */
public class TimetableFragment extends Fragment {
    private List<TabItem> mTabs = new ArrayList<>();

    public TimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        final int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        final int nextWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        final Bundle bundle_1 = new Bundle();
        final Bundle bundle_2 = new Bundle();
        bundle_1.putInt(Const.BundleParams.TIMETABLE_WEEK, currentWeek);
        bundle_2.putInt(Const.BundleParams.TIMETABLE_WEEK, nextWeek);
        mTabs.add(new TabItem(
                getResources().getString(R.string.timetable_current_week, currentWeek),
                TimetableOverviewFragment.class,
                bundle_1
        ));
        mTabs.add(new TabItem(
                getResources().getString(R.string.timetable_next_week, nextWeek),
                TimetableOverviewFragment.class,
                bundle_2
        ));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        // Setze Toolbartitle
        ((INavigation)getActivity()).setTitle(getResources().getString(R.string.navi_timetable));

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        // Adapter für Tabs erstellen und an view hängen
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), mTabs);
        viewPager.setAdapter(viewPagerAdapter);

        // TabLayout "stylen"
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        // Stetze feste Anzahl an Tabs (Tabs wirken nciht angeklatscht)
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        // Tabs nehemen immer die ganze Breite ein
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
