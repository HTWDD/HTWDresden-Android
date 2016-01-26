package de.htwdd.htwdresden;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.TimetableUserDAO;
import de.htwdd.htwdresden.interfaces.INavigation;

public class TimetableEditActivity extends AppCompatActivity implements INavigation {
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Parameter holen
        Bundle bundle = getIntent().getExtras();
        int ds = bundle.getInt(Const.BundleParams.TIMETABLE_DS, 1);
        int day = bundle.getInt(Const.BundleParams.TIMETABLE_DAY, 1);
        int week = bundle.getInt(Const.BundleParams.TIMETABLE_WEEK, 1);
        boolean edit = bundle.getBoolean(Const.BundleParams.TIMETABLE_EDIT, false);
        boolean create = bundle.getBoolean(Const.BundleParams.TIMETABLE_CREATE, false);

        // Anzahl möglicher Stunden aus DB bestimmen um zu entscheiden welches Fragment angezeigt werden soll
        DatabaseManager databaseManager = new DatabaseManager(this);
        TimetableUserDAO timetableUserDAO = new TimetableUserDAO(databaseManager);
        long count = timetableUserDAO.countDS(week, day, ds);
        Fragment fragment;

        // Neue Stunden anlegen
        if (create || count == 0) {
            bundle.putBoolean(Const.BundleParams.TIMETABLE_CREATE, true);
            fragment = new TimetableEditFragment();
        }
        // Stunde bearbeiten
        else if (edit && count == 1)
            fragment = new TimetableEditFragment();
        else
            // Übersicht anzeigen (auch bei bearbeiten wenn mehrere Einträge vorhanden sind)
            fragment = new TimetableDetailsFragment();
        fragment.setArguments(bundle);

        // Bei orientation change Fragment nicht neuladen
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.activity_sync_FrameLayout, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                setResult(Activity.RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setTitle(String title) {
        if (title == null || title.isEmpty())
            actionBar.setTitle(R.string.app_name);
        else actionBar.setTitle(title);
    }

    @Override
    public void setNavigationItem(int item) {
    }
}
