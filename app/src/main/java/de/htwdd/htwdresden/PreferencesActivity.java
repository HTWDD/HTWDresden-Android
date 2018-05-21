package de.htwdd.htwdresden;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setSupportActionBar(findViewById(R.id.my_awesome_toolbar));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.navi_head_settings);
        }

        // Bei orientation change Fragment nicht neuladen
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.activity_sync_FrameLayout, new PreferencesFragment()).commit();
        }
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
}
