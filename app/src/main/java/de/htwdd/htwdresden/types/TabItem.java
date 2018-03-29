package de.htwdd.htwdresden.types;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Die Klasse beschreibt (charakterisiert) einen Tab im ViewPagerAdapter
 *
 * @author Kay Förster
 * @version 1.0
 */
public class TabItem {
    private CharSequence title;
    private Class maClass;
    private Bundle bundle;

    public TabItem(@NonNull final CharSequence title, @NonNull final Class aclass, @Nullable final Bundle bundle) {
        this.title = title;
        maClass = aclass;
        this.bundle = bundle;
    }

    @Nullable
    public Fragment createFragment() {
        try {
            final Fragment fragment = (Fragment) maClass.newInstance();
            fragment.setArguments(bundle);
            return fragment;
        } catch (final Exception e) {
            Log.e("TabItem", "Fehler beim erstellen eines Fragments", e);
        }
        return null;
    }

    public CharSequence getTitle() {
        return title;
    }
}
