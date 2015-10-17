package de.htwdd.htwdresden;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.htwdd.htwdresden.adapter.NavigationDrawerAdapter;
import de.htwdd.htwdresden.types.NavigationDrawerItem;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mNavigation_drawer_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar einfügen
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Elemente für NavigationDrawer anlegen
        List<NavigationDrawerItem> dataList = new ArrayList<>();
        dataList.add(new NavigationDrawerItem("Übersicht", R.drawable.ic_home_24dp));
        dataList.add(new NavigationDrawerItem("Uni Alltag"));
        dataList.add(new NavigationDrawerItem("Mensa", R.drawable.food));
        dataList.add(new NavigationDrawerItem("Stundenplan", R.drawable.ic_access_time_24dp));
        dataList.add(new NavigationDrawerItem("Noten / Prüfungen", R.drawable.ic_mode_edit_24dp));
        dataList.add(new NavigationDrawerItem("Verwaltung", R.drawable.ic_supervisor_account_24dp));
        dataList.add(new NavigationDrawerItem("Optionen"));
        dataList.add(new NavigationDrawerItem("Einstellungen", R.drawable.ic_settings_24dp));
        dataList.add(new NavigationDrawerItem("Über die App", R.drawable.ic_info_outline_24dp));

        // Hole Views
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.navigation_drawer);
        mNavigation_drawer_left = (LinearLayout) findViewById(R.id.navigation_drawer_left);

        // Adapter für Navigationselemente erstellen
        NavigationDrawerAdapter drawerAdapter = new NavigationDrawerAdapter(this, R.layout.navigation_drawer_item, dataList);

        // Adapter Liste zuordnen
        mDrawerList.setAdapter(drawerAdapter);

        // Klicks auf Items behandeln, um Fragments / Aktivity zu ändern / starten
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        // Actionbar Titel anpassen
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                // Schließe Tastatur
                if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Setze Start-Fragment
        if (savedInstanceState == null)
            selectItem(0);
    }

    private void selectItem(int position) {
        // Item markieren
        mDrawerList.setItemChecked(position, true);

        // NavigationDrawer schliesen
        mDrawerLayout.closeDrawer(mNavigation_drawer_left);
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
}
