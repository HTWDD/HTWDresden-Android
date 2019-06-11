package de.htwdd.htwdresden.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import de.htwdd.htwdresden.types.TabItem;


public class ViewPagerAdapter extends FragmentPagerAdapter {
    // Liste der Tabs welche angezeigt werden
    private List<TabItem> mTabs;
    private boolean updateProzess = false;

    public ViewPagerAdapter(@NonNull final FragmentManager fragmentManager, @NonNull final List<TabItem> mTabs) {
        super(fragmentManager);
        this.mTabs = mTabs;
    }

    @Override
    public Fragment getItem(final int position) {
        return mTabs.get(position).createFragment();
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return mTabs.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public int getItemPosition(@NonNull final Object object) {
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
