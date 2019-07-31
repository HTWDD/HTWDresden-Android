package de.htwdd.htwdresden;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import de.htwdd.htwdresden.adapter.ViewPagerAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.TabItem;

import static de.htwdd.htwdresden.MealDetailListFragment.ARG_CANTEEN_ID;


/**
 * Übersicht über die verschiedenen Mensa-Funktionalitäten
 *
 * @author Kay Förster
 */
public class MealFragment extends Fragment {
    private List<TabItem> mTabs = new ArrayList<>();

    public MealFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundleMealDay = new Bundle();
        bundleMealDay.putInt(Const.BundleParams.MENSA_DETAIL_MODE, 0);
        bundleMealDay.putInt(ARG_CANTEEN_ID, Integer.parseInt(getArguments().getString(ARG_CANTEEN_ID)));
        final Bundle bundleMealWeek = new Bundle();
        bundleMealWeek.putInt(Const.BundleParams.MENSA_DETAIL_MODE, 1);
        bundleMealWeek.putInt(ARG_CANTEEN_ID, Integer.parseInt(getArguments().getString(ARG_CANTEEN_ID)));
        final Bundle bundleMealNextWeek = new Bundle();
        bundleMealNextWeek.putInt(Const.BundleParams.MENSA_DETAIL_MODE, 2);
        bundleMealNextWeek.putInt(ARG_CANTEEN_ID, Integer.parseInt(getArguments().getString(ARG_CANTEEN_ID)));
        mTabs.add(new TabItem(
                getResources().getString(R.string.mensa_tab_today),
                MealDetailListFragment.class,
                bundleMealDay
        ));
        mTabs.add(new TabItem(
                getResources().getString(R.string.mensa_tab_this_week),
                MensaDetailWeekFragment.class,
                bundleMealWeek
        ));
        mTabs.add(new TabItem(
                getResources().getString(R.string.mensa_tab_next_week),
                MensaDetailWeekFragment.class,
                bundleMealNextWeek
        ));
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tabs, container, false);
        final ViewPager viewPager = view.findViewById(R.id.viewpager);

        // Adapter für Tabs erstellen und an view hängen
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), mTabs));

        // TabLayout "stylen"
        final TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        // Setze feste Anzahl an Tabs
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        // Tabs nehmen immer die ganze Breite ein
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
