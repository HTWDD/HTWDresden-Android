package de.htwdd.htwdresden;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import de.htwdd.htwdresden.adapter.WizardSectionsPagerAdapter;

/**
 * Created by Meralium on 24.05.16.
 */
public class WizardActivity extends AppCompatActivity {

    private ImageView circles[];
    private ImageButton forwards;
    private ImageButton backwards;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private boolean mVisible;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private WizardSectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_activity);

        // Initialize navigation elements and listeners for them
        forwards = (ImageButton) findViewById(R.id.wizard_button_forwards);
        backwards = (ImageButton) findViewById(R.id.wizard_button_backwards);
        initListeners();
        initCircles();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new WizardSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_wizard);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setCirclesAlpha(position);
                setArrowsAlpha(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();

        delayedHide(100);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void initListeners() {
        if (forwards == null || backwards == null) return;
        forwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPageNumber = mViewPager.getCurrentItem() + 1;
                if (newPageNumber < mSectionsPagerAdapter.getCount()) {
                    mViewPager.setCurrentItem(newPageNumber, true);
                }
            }
        });
        backwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPageNumber = mViewPager.getCurrentItem() - 1;
                if (newPageNumber >= 0) {
                    mViewPager.setCurrentItem(newPageNumber, true);
                }
            }
        });
    }

    private void initCircles() {
        circles = new ImageView[4];
        circles[0] = (ImageView) findViewById(R.id.progress_circle_1);
        circles[1] = (ImageView) findViewById(R.id.progress_circle_2);
        circles[2] = (ImageView) findViewById(R.id.progress_circle_3);
        circles[3] = (ImageView) findViewById(R.id.progress_circle_4);
    }

    private void setCirclesAlpha(int pageNumber) {
        if (circles == null) return;
        for (int i = 0; i < circles.length; i++) {
            circles[i].setAlpha(.5f);
            if (i == pageNumber)
                circles[i].setAlpha(1.f);
        }
    }

    private void setArrowsAlpha(int pageNumber) {
        backwards.setAlpha(1f);
        forwards.setAlpha(1f);

        if (pageNumber == 0) {
            backwards.setAlpha(.5f);
        }
        if (pageNumber == mSectionsPagerAdapter.getCount() - 1) {
            forwards.setAlpha(0.5f);
        }
    }

}
