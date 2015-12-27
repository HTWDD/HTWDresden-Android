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
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.IToolbarTitel;
import de.htwdd.htwdresden.types.TabItem;


/**
 * Hauptfragment welches die verschiedenen Sub-Fragmente enthält
 */
public class MensaFragment extends Fragment {
    private List<TabItem> mTabs = new ArrayList<>();

    public MensaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle_1 = new Bundle();
        bundle_1.putInt(Const.BundleParams.MENSA_DETAIL_MODE, 0);
        Bundle bundle_2 = new Bundle();
        bundle_2.putInt(Const.BundleParams.MENSA_DETAIL_MODE, 1);
        Bundle bundle_3 = new Bundle();
        bundle_3.putInt(Const.BundleParams.MENSA_DETAIL_MODE, 2);
        mTabs.add(new TabItem(
                getResources().getString(R.string.mensa_tab_today),
                MensaDetailFragment.class,
                bundle_1
        ));
        mTabs.add(new TabItem(
                getResources().getString(R.string.mensa_tab_this_week),
                MensaDetailFragment.class,
                bundle_2
        ));
        mTabs.add(new TabItem(
                getResources().getString(R.string.mensa_tab_next_week),
                MensaDetailFragment.class,
                bundle_3
        ));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mensa, container, false);

        // Setze Toolbartitle
        ((IToolbarTitel)getActivity()).setTitle(getResources().getString(R.string.navi_mensa));

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
