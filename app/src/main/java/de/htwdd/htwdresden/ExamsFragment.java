package de.htwdd.htwdresden;


import android.app.Fragment;
import android.os.Bundle;
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
 */
public class ExamsFragment extends Fragment {
    private List<TabItem> mTabs = new ArrayList<>();

    public ExamsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabs.add(new TabItem(
                getResources().getString(R.string.exams_grade),
                Fragment.class,
                null
        ));
        mTabs.add(new TabItem(
                getResources().getString(R.string.exams_statistic),
                Fragment.class,
                null
        ));
        mTabs.add(new TabItem(
                getResources().getString(R.string.exams_exams),
                Fragment.class,
                null
        ));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        // Setze Toolbartitle
        ((INavigation)getActivity()).setTitle(getResources().getString(R.string.navi_exams));

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        // Adapter für Tabs erstellen und an view hängen
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), mTabs);
        viewPager.setAdapter(viewPagerAdapter);

        // TabLayout "stylen"
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        // Stetze feste Anzahl an Tabs (Tabs wirken nciht angeklatscht)
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        // Tabs nehemen immer die ganze Breite ein
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
