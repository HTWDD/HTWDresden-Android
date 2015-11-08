package de.htwdd.htwdresden.types;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Die Klasse beschreibt (charakterisiert) einen Tab im ViewPagerAdapter
 *
 * @author Kay Förster
 * @version 1.0
 */
public class TabItem {
    private CharSequence mTitle;
    private Class maClass;
    private Bundle bundle;

    public TabItem(final CharSequence title, final Class aclass, @Nullable final Bundle bundle) {
        mTitle = title;
        maClass = aclass;
        this.bundle = bundle;
    }

    public Fragment createFragment() {
        try {
            Fragment fragment = (Fragment) maClass.newInstance();
            fragment.setArguments(bundle);
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CharSequence getmTitle() {
        return mTitle;
    }
}
