package de.htwdd.htwdresden;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.types.LessonUser;
import io.realm.Realm;
import io.realm.RealmResults;

public class TimetableEditActivity extends AppCompatActivity implements INavigation {
    private ActionBar actionBar;
    private Realm realm;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Toolbar setzen
        setSupportActionBar((Toolbar) findViewById(R.id.my_awesome_toolbar));
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Datenbankverbindung
        realm = Realm.getDefaultInstance();

        // Parameter holen
        final Bundle bundle = getIntent().getExtras();
        boolean edit = bundle.getBoolean(Const.BundleParams.TIMETABLE_EDIT, false);
        Fragment fragment;

        // Anzahl möglicher Stunden aus DB bestimmen um zu entscheiden welches Fragment angezeigt werden soll
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        calendar.set(Calendar.DAY_OF_WEEK, bundle.getInt(Const.BundleParams.TIMETABLE_DAY, 1) + 1);
        calendar.set(Calendar.WEEK_OF_YEAR, bundle.getInt(Const.BundleParams.TIMETABLE_WEEK, 1));

        final RealmResults<LessonUser> results = TimetableHelper.getLessonsByDateAndDs(
                realm,
                calendar,
                bundle.getInt(Const.BundleParams.TIMETABLE_DS, 1),
                bundle.getBoolean(Const.BundleParams.TIMETABLE_FILTER_CURRENT_WEEK, true),
                bundle.getBoolean(Const.BundleParams.TIMETABLE_FILTER_SHOW_HIDDEN, false));
        final int countResults = results.size();

        // Entscheiden welches Fragment eingebunden werden soll
        if (countResults == 0) {
            edit = false;
            fragment = new TimetableEditFragment();
        } else if (edit && countResults == 1) {
            bundle.putString(Const.BundleParams.TIMETABLE_LESSON_ID, results.first().getId());
            fragment = new TimetableEditFragment();
        } else {
            fragment = new TimetableDetailsFragment();
        }

        // Parameter übergeben
        bundle.putBoolean(Const.BundleParams.TIMETABLE_EDIT, edit);
        fragment.setArguments(bundle);

        // Bei orientation change Fragment nicht neuladen
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.activity_sync_FrameLayout, fragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        final int id = item.getItemId();

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
    public void setTitle(@Nullable final String title) {
        if (title == null || title.isEmpty())
            actionBar.setTitle(R.string.app_name);
        else actionBar.setTitle(title);
    }

    @Override
    public void setNavigationItem(int item) {
    }

    @Override
    public void goToNavigationItem(int item) {
    }
}
