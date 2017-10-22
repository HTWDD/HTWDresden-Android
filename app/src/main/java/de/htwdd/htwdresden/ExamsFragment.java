package de.htwdd.htwdresden;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.htwdd.htwdresden.adapter.ViewPagerAdapter;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.TabItem;

/**
 * Hauptfragment für Noten & Prüfungen welches die verschiedenen Sub-Fragmente enthält
 * @author Kay Förster
 */
public class ExamsFragment extends Fragment {
    private final List<TabItem> mTabs = new ArrayList<>();

    public ExamsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabs.add(new TabItem(
                getResources().getString(R.string.exams_grade),
                ExamResultFragment.class,
                null
        ));
        mTabs.add(new TabItem(
                getResources().getString(R.string.exams_statistic),
                ExamResultStatsFragment.class,
                null
        ));
        mTabs.add(new TabItem(
                getResources().getString(R.string.exams_exams),
                ExamsListFragment.class,
                null
        ));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        // Setze Title der Toolbar
        ((INavigation)getActivity()).setTitle(getResources().getString(R.string.navi_exams));

        // Adapter für Tabs erstellen und an view hängen
        final ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), mTabs));

        // TabLayout "stylen"
        final TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        // Setze feste Anzahl an Tabs (Tabs wirken nicht angeklatscht)
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        // Tabs nehmen immer die ganze Breite ein
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
