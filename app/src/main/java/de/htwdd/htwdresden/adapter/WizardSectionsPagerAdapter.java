package de.htwdd.htwdresden.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;

import de.htwdd.htwdresden.WizardFinalStateFragment;
import de.htwdd.htwdresden.WizardStgSettingsFragment;
import de.htwdd.htwdresden.WizardUserDataSettingsFragment;
import de.htwdd.htwdresden.WizardWelcomeFragment;
import de.htwdd.htwdresden.types.dataBinding.WizardDataBindingObject;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class WizardSectionsPagerAdapter extends FragmentPagerAdapter {
    private final WizardDataBindingObject dataBindingObject;

    public WizardSectionsPagerAdapter(@NonNull final FragmentManager fm, @NonNull final WizardDataBindingObject dataBindingObject) {
        super(fm);
        this.dataBindingObject = dataBindingObject;
    }

    @Override
    public Fragment getItem(final int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = WizardStgSettingsFragment.newInstance(dataBindingObject);
                break;
            case 2:
                fragment = WizardUserDataSettingsFragment.newInstance(dataBindingObject);
                break;
            case 3:
                fragment = WizardFinalStateFragment.newInstance(dataBindingObject);
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
