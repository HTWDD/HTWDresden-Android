package de.htwdd.htwdresden.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.htwdd.htwdresden.WizardFinalStateFragment;
import de.htwdd.htwdresden.WizardStgSettingsFragment;
import de.htwdd.htwdresden.WizardUserDataSettingsFragment;
import de.htwdd.htwdresden.WizardWelcomeFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class WizardSectionsPagerAdapter extends FragmentPagerAdapter {

    public WizardSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        // TODO: 25.05.16 save data in bundels on stop  and read on fragmentStart
        switch (position) {
            case 0:
                fragment = WizardWelcomeFragment.newInstance(position + 1);
                break;
            case 1:
                fragment = new WizardStgSettingsFragment();
                break;
            case 2:
                fragment = new WizardUserDataSettingsFragment();
                break;
            case 3:
                fragment = new WizardFinalStateFragment();
                break;
            default:
                fragment = WizardWelcomeFragment.newInstance(position + 1);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }
}
