package de.htwdd.htwdresden;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.Tracking;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.User;

/**
 * Hinweis zum Navigation Drawer:
 * Das Highlighting ist aktuell in der Support-Libary nicht vollständig / richtig implentiert,
 * darum manuelle Behandlung im Code
 *
 * @see <a href="https://guides.codepath.com/android/Fragment-Navigation-Drawer#limitations">Navigation Drawer Limitations</a>
 */

public class MainActivity extends AppCompatActivity implements INavigation, HTWDDEventsProfileFragment.OnFragmentInteractionListener {
    private Activity mActivity;
    private DrawerLayout mDrawerLayout;
    private MenuItem mPreviousMenuItem;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Makriere im NavigationDrawer
            setNavigationItem(item);
            // Ändere Inhalt
            selectFragment(item.getItemId());

            return false;
        }
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Hole Views
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

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
                if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        setupDrawerContent(mNavigationView);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // Beim App-Start ein spezielles Fragment öffnen?
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Const.IntentParams.START_WITH_FRAGMENT)) {
            goToNavigationItem(intent.getIntExtra(Const.IntentParams.START_WITH_FRAGMENT, R.id.navigation_overview));
        }
        // Setze Start-Fragment
        else if (savedInstanceState == null) {
            onNavigationItemSelectedListener.onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.navigation_overview));
            selectFragment(R.id.navigation_overview);
            mPreviousMenuItem = mNavigationView.getMenu().findItem(R.id.navigation_overview);
            mPreviousMenuItem.setChecked(true);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private void selectFragment(final int position) {
        final FragmentManager fragmentManager = getFragmentManager();
        // Lösche BackStack, ansonsten kommt es zu Überblendungen wenn Menü-Auswahl und Backtaste verwendet wird.
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment fragment;
                String tag = null;

                switch (position) {
                    case R.id.navigation_overview:
                        fragment = new OverviewFragment();
                        tag = "overview";
                        break;
                    case R.id.navigation_mensa:
                        fragment = new MensaFragment();
                        break;
                    case R.id.navigation_htwEvents:
                        fragment = new HTWDDEventsProfileFragment();
                        fragment = changeFragementIfNorSignedUp(fragment);
                        //----------------für das Toolbar-Ausblenden aber halt überall
                        /*HTWDDEventsEventCreator.setOnEventListener(new HTWDDEventsEventCreator.OnEventListener() {
                            @Override
                            public void hideToolbar() {
                                getSupportActionBar().hide();
                            }
                        });*/
                        break;
                    case R.id.navigation_htwEventsEventCreator:
                        fragment = new HTWDDEventsEventCreator();
                        break;
                    case R.id.navigation_timetable:
                        fragment = new TimetableFragment();
                        break;
                    case R.id.navigation_room_timetable:
                        fragment = new RoomTimetableFragment();
                        break;
                    case R.id.navigation_exams:
                        fragment = new ExamsFragment();
                        break;
                    case R.id.navigation_campus_plan:
                        fragment = new CampusPlanFragment();
                        break;
                    case R.id.navigation_settings:
                        fragment = new SettingsFragment();
                        break;
                    case R.id.navigation_about:
                        fragment = new AboutFragment();
                        break;
                    case R.id.navigation_uni_administration:
                        fragment = new ManagementFragment();
                        break;
                    default:
                        fragment = new Fragment();
                        break;
                }

                // Fragment ersetzen
                fragmentManager.beginTransaction().replace(R.id.activity_main_FrameLayout, fragment, tag).commitAllowingStateLoss();
            }
        }, 250);

        // NavigationDrawer schliesen
        mDrawerLayout.closeDrawers();
    }

    private Fragment changeFragementIfNorSignedUp(Fragment fragment) {
        if (!User.isThereSNrAndPassw(getApplication())) {
            fragment = new HTWDDEventsWizard();
        } else if (!User.isUserSignedUp(MainActivity.this)) {
            fragment = new HTWDDEventsSignUp();
        }
        return fragment;
    }

    /**
     * Markiert das übergebene MenuItem im NavigationDraver
     *
     * @param item Item welches markiert werden soll oder null falls Markierung aufgehoben werden soll
     */
    private void setNavigationItem(final MenuItem item) {
        // Markiere aktuelles Feld
        if (mPreviousMenuItem != null) {
            mPreviousMenuItem.setChecked(false);
        }
        mPreviousMenuItem = item;
        if (item != null)
            item.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();

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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            if (savedInstanceState.containsKey("mPreviousMenuItem")) {
                NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
                mPreviousMenuItem = mNavigationView.getMenu().findItem(R.id.navigation_overview);
                mPreviousMenuItem.setChecked(false);
                mPreviousMenuItem = mNavigationView.getMenu().findItem(savedInstanceState.getInt("mPreviousMenuItem"));
                mPreviousMenuItem.setChecked(true);
            }
    }

    @Override
    public void setTitle(final String title) {
        if (title == null || title.isEmpty())
            actionBar.setTitle(R.string.app_name);
        else actionBar.setTitle(title);
    }

    @Override
    public void setNavigationItem(int item) {
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        setNavigationItem(mNavigationView.getMenu().findItem(item));
    }

    @Override
    public void goToNavigationItem(@IdRes final int item) {
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        onNavigationItemSelectedListener.onNavigationItemSelected(mNavigationView.getMenu().findItem(item));
    }

    public void onSignupButton(View view) {
        EditText nicknameEText = (EditText) findViewById(R.id.htwddevents_signup_nickname);
        EditText firstnameEText = (EditText) findViewById(R.id.htwddevents_signup_firstname);
        EditText lastnameEText = (EditText) findViewById(R.id.htwddevents_signup_lastname);
        EditText stgEText = (EditText) findViewById(R.id.htwddevents_signup_studiengang);
        String nickname = nicknameEText.getText().toString();

        if (nickname.length() < HTWDDEventsSignUp.NICKNAME_LENGTH) {
            Toast.makeText(MainActivity.this, "Nickname > 3", Toast.LENGTH_SHORT).show();
            return;
        }
        if (firstnameEText.length() < HTWDDEventsSignUp.FIRSTNAME_LENGTH) {
            Toast.makeText(MainActivity.this, "Bitte, den Vornamen eingeben oder auf Überspringen drücken.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastnameEText.length() < HTWDDEventsSignUp.LASTNAME_LENGTH) {
            Toast.makeText(MainActivity.this, "Bitte, den Namen eingeben oder auf Überspringen drücken.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (stgEText.length() < HTWDDEventsSignUp.STG_LENGTH) {
            Toast.makeText(MainActivity.this, "Studiengang eingeben.", Toast.LENGTH_SHORT).show();
        }
        Log.e("YOUT INTERED", nickname + ";");


        if (/*sendToPHPScript(daten)*/true) {
            //setSignedUpPref(this,true);
            //Const.HTWEvents.goToFragment(this, new HTWDDEventsProfileFragment());
        } else {

            //bleiben in diesem Fragement
        }
    }

    private void setSignedUpPref(Activity activity, boolean is) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean("SignedUp", is);
    }

    @Override
    public void onProfileFragmentInteraction(String string) {

    }

    public void onSaveWizard(View view) {
        final FragmentManager fragmentManager = getFragmentManager();
        TextView snummerTV = (TextView) findViewById(R.id.htwddevents_wizard_snummer);
        TextView passwTV = (TextView) findViewById(R.id.htwddevents_wizard_passw);

        User.setUserNameSP(getApplicationContext(), snummerTV.getText().toString());
        User.setUserPasswSP(getApplicationContext(), passwTV.getText().toString());

        Fragment fragment = new HTWDDEventsSignUp();
        fragmentManager.beginTransaction().replace(R.id.activity_main_FrameLayout, fragment, "SIGN UP").commitAllowingStateLoss();

    }
}
