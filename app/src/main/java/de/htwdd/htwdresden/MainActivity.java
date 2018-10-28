package de.htwdd.htwdresden;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.Tracking;
import de.htwdd.htwdresden.interfaces.INavigation;

/**
 * Hinweis zum Navigation Drawer:
 * Das Highlighting ist aktuell in der Support-Library nicht vollständig / richtig implementiert,
 * darum manuelle Behandlung im Code
 *
 * @see <a href="https://guides.codepath.com/android/Fragment-Navigation-Drawer#limitations">Navigation Drawer Limitations</a>
 */

public class MainActivity extends AppCompatActivity implements INavigation {
    private DrawerLayout mDrawerLayout;
    private MenuItem mPreviousMenuItem;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private final NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = item -> {
        final int resId = item.getItemId();
        // Activity starten
        final boolean result = selectActivity(resId);
        // Wenn keine Activity gestartet wurde, Fragments suchen
        if (!result) {
            // Markiere im NavigationDrawer
            setNavigationItem(item);
            // Ändere Inhalt
            selectFragment(resId);
        }
        return false;
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Einfache Nutzeranalyse
        Tracking.makeRequest(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar einfügen
        setSupportActionBar(findViewById(R.id.my_awesome_toolbar));

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Hole Views
        mDrawerLayout = findViewById(R.id.drawerLayout);
        final NavigationView mNavigationView = findViewById(R.id.navigation_view);

        // Wenn Views nicht gefunden, sofort abbrechen
        assert mNavigationView != null && mDrawerLayout != null;

        // Actionbar Titel anpassen
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {
            private CharSequence title;

            public void onDrawerClosed(View view) {
                if (actionBar.getTitle() != null && actionBar.getTitle().equals(getString(R.string.app_name)))
                    actionBar.setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                title = actionBar.getTitle();
                actionBar.setTitle(R.string.app_name);

                // Schließe Tastatur
                final View currentFocus = getCurrentFocus();
                if (currentFocus instanceof EditText) {
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        // Beim App-Start ein spezielles Fragment öffnen?
        final Intent intent = getIntent();
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Const.IntentParams.START_ACTION_TIMETABLE:
                    goToNavigationItem(R.id.navigation_timetable);
                    intent.setAction("");
                    return;
                case Const.IntentParams.START_ACTION_MENSA:
                    goToNavigationItem(R.id.navigation_mensa);
                    intent.setAction("");
                    return;
                case Const.IntentParams.START_ACTION_EXAM_RESULTS:
                    goToNavigationItem(R.id.navigation_exams);
                    intent.setAction("");
                    return;
            }
        }
        // Setze Start-Fragment
        if (savedInstanceState == null) {
            onNavigationItemSelectedListener.onNavigationItemSelected(getMenu(mNavigationView).findItem(R.id.navigation_overview));
            mPreviousMenuItem = getMenu(mNavigationView).findItem(R.id.navigation_overview);
            mPreviousMenuItem.setChecked(true);
        }
    }

    private void selectFragment(@IdRes final int position) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment supportedFragment;
        String tag = null;

        switch (position) {
            case R.id.navigation_overview:
                supportedFragment = new OverviewFragment();
                setTitle(getString(R.string.navi_overview));
                tag = "overview";
                break;
            case R.id.navigation_mensa:
                supportedFragment = new MensaFragment();
                setTitle(getString(R.string.navi_mensa));
                break;
            case R.id.navigation_timetable:
                supportedFragment = new TimetableFragment();
                setTitle(getString(R.string.navi_timetable));
                break;
            case R.id.navigation_room_timetable:
                supportedFragment = new RoomTimetableFragment();
                setTitle(getString(R.string.navi_room_timetable));
                break;
            case R.id.navigation_exams:
                supportedFragment = new ExamsFragment();
                setTitle(getString(R.string.navi_exams));
                break;
            case R.id.navigation_campus_plan:
                supportedFragment = new CampusPlanFragment();
                setTitle(getString(R.string.navi_campus));
                break;
            case R.id.navigation_about:
                supportedFragment = new AboutFragment();
                setTitle(getString(R.string.navi_about));
                break;
            case R.id.navigation_uni_administration:
                supportedFragment = new ManagementFragment();
                setTitle(getString(R.string.navi_uni_administration));
                break;
            default:
                supportedFragment = new Fragment();
                break;
        }

        // Lösche BackStack, ansonsten kommt es zu Überblendungen wenn Menü-Auswahl und Backbutton verwendet wird.
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(R.id.activity_main_FrameLayout, supportedFragment, tag).commitAllowingStateLoss();

        // NavigationDrawer schließen
        mDrawerLayout.closeDrawers();
    }

    /**
     * Prüft ob Auswahl eine Activity starten soll und startet diese ggf.
     *
     * @param position Ausgewähltes Item
     * @return True wenn Activity gefunden wurde, sonst false
     */
    private boolean selectActivity(@IdRes final int position) {
        if (position == R.id.navigation_settings) {
            final Intent startPreferences = new Intent(this, PreferencesActivity.class);
            startActivity(startPreferences);
            return true;
        }
        return false;
    }

    /**
     * Markiert das übergebene MenuItem im NavigationDrawer
     *
     * @param item Item welches markiert werden soll oder null falls Markierung aufgehoben werden soll
     */
    private void setNavigationItem(@Nullable final MenuItem item) {
        // Markiere aktuelles Feld
        if (mPreviousMenuItem != null) {
            mPreviousMenuItem.setChecked(false);
        }
        mPreviousMenuItem = item;
        if (item != null)
            item.setChecked(true);
    }

    /**
     * Liefert Menu für die Navigation
     *
     * @param mNavigationView View der Navigation
     * @return Navigationsmenü
     */
    private Menu getMenu(@Nullable NavigationView mNavigationView) {
        if (mNavigationView == null)
            mNavigationView = findViewById(R.id.navigation_view);

        // Wenn mNavigationView nicht gefunden wird Ausnahme werfen
        assert mNavigationView != null;

        return mNavigationView.getMenu();
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() == 0)
            // Wenn das Übersichtsfragment das aktuelle ist, die App beenden
            if (fragmentManager.findFragmentByTag("overview") != null)
                finish();
            else {
                // Zur Übersichtsseite springen
                goToNavigationItem(R.id.navigation_overview);
            }
        else fragmentManager.popBackStack();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mPreviousMenuItem != null)
            outState.putInt("mPreviousMenuItem", mPreviousMenuItem.getItemId());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            if (savedInstanceState.containsKey("mPreviousMenuItem")) {
                Menu menu = getMenu(null);
                mPreviousMenuItem = menu.findItem(R.id.navigation_overview);
                mPreviousMenuItem.setChecked(false);
                mPreviousMenuItem = menu.findItem(savedInstanceState.getInt("mPreviousMenuItem"));
                mPreviousMenuItem.setChecked(true);
            }
    }

    @Override
    public void setTitle(@Nullable final String title) {
        if (title == null || title.isEmpty())
            actionBar.setTitle(R.string.app_name);
        else actionBar.setTitle(title);
    }

    @Override
    public void goToNavigationItem(@IdRes final int item) {
        onNavigationItemSelectedListener.onNavigationItemSelected(getMenu(null).findItem(item));
    }
}
