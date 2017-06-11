package de.htwdd.htwdresden;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.htwdd.htwdresden.interfaces.INavigation;

public class RoomTimetableDetailsActivity extends AppCompatActivity implements INavigation {
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setSupportActionBar((Toolbar) findViewById(R.id.my_awesome_toolbar));

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Bei orientation change Fragment nicht neuladen
        if (savedInstanceState == null) {
            final Fragment fragment = new RoomTimetableDetailsFragment();
            fragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().replace(R.id.activity_sync_FrameLayout, fragment).commit();
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
