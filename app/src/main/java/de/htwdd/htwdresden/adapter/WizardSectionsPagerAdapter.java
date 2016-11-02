package de.htwdd.htwdresden.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;

import de.htwdd.htwdresden.WizardFinalStateFragment;
import de.htwdd.htwdresden.WizardStgSettingsFragment;
import de.htwdd.htwdresden.WizardUserDataSettingsFragment;
import de.htwdd.htwdresden.WizardWelcomeFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class WizardSectionsPagerAdapter extends FragmentPagerAdapter {
    private final Bundle bundle;

    public WizardSectionsPagerAdapter(@NonNull final FragmentManager fm, @NonNull final Bundle bundle) {
        super(fm);
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(final int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = WizardStgSettingsFragment.newInstance(bundle);
                break;
            case 2:
                fragment = WizardUserDataSettingsFragment.newInstance(bundle);
                break;
            case 3:
                fragment = new WizardFinalStateFragment();
                break;
            case 0:
            default:
                fragment = WizardWelcomeFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
