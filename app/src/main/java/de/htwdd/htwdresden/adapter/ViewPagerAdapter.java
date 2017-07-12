package de.htwdd.htwdresden.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.List;

import de.htwdd.htwdresden.types.TabItem;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    // Liste der Tabs welche angezeigt werden
    private List<TabItem> mTabs;
    private boolean updateProzess = false;

    public ViewPagerAdapter(FragmentManager fm, List<TabItem> mTabs) {
        super(fm);
        this.mTabs = mTabs;
    }

    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position).createFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getmTitle();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        if (updateProzess)
            return POSITION_NONE;
        return super.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        updateProzess = true;
        super.notifyDataSetChanged();
        updateProzess = false;
    }
}
