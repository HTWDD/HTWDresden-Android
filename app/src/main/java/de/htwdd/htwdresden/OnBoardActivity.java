package de.htwdd.htwdresden;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.core.content.res.ResourcesCompat;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

import de.htwdd.htwdresden.account.AuthenticatorActivity;

public class OnBoardActivity extends TutorialActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button buttonPrev;
    Button buttonNext;
    Button btnAnalytics;
    Button btnCrashlytics;
    Button btnLogin;
    Drawable active;
    Drawable inActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        active = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_button_active, null);
        inActive = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_button_inactive, null);

        setIndicator(R.drawable.indicator_selected_inactive);
        setIndicatorSelected(R.drawable.indicator_selected);
        int i = 0;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

//        if(sharedPreferences.getBoolean("FIRST_RUN", true)){
        if(i!=1){
            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_welcome)
                    .setTitle(getString(R.string.welcome))
                    .setContent(getString(R.string.welcome_text_content))
                    .setDrawable(R.drawable.htw_logo_round_blue)
                    .build());
            // Permission Step
            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_analytics)
                    .setTitle(getString(R.string.analytics))
                    .setContent(getString(R.string.analytics_text_content))
                    .setSummary(getString(R.string.analytics_text_summary))
                    .setDrawable(R.drawable.htw_logo_round_blue)
                    .build());

            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_crashlogger)
                    .setTitle(getString(R.string.crashlytics))
                    .setContent(getString(R.string.crashlytics_text_content))
                    .setSummary(getString(R.string.crashlytics_text_summary))
                    .setDrawable(R.drawable.htw_logo_round_blue)
                    .build());

            addFragment(new Step.Builder()
                    .setTitle(getString(R.string.login_with_img))
                    .setView(R.layout.onboarding_fragment_login)
                    .setContent(getString(R.string.login_text_content))
                    .setDrawable(R.drawable.htw_logo_round_blue)
                    .setSummary(getString(R.string.login_text_summary))
                    .build());

            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_finish_tutorial)
                    .setTitle("Activate Crashlogger?")
                    .setContent("This is content")
                    .setSummary("This is summary")
                    .setDrawable(R.drawable.htw_logo_round_blue)
                    .build());

            buttonPrev = findViewById(R.id.prev);
            buttonPrev.setTextColor(getResources().getColor(R.color.middle_gray));
            buttonPrev.setVisibility(View.GONE);
            buttonNext = findViewById(R.id.next);
            buttonNext.setTextColor(getResources().getColor(R.color.middle_gray));
            setPrevText("Zurück");
            setNextText("Weiter");
            setFinishText("Fertig");
        }
        else {
            finishTutorial();
        }
    }

    @Override
    public void currentFragmentPosition(int position) {
        if(position == 0) {
            buttonPrev.setVisibility(View.GONE);
        }
        else {
            buttonPrev.setVisibility(View.VISIBLE);
        }

        if(position == 1) {
            btnAnalytics = findViewById(R.id.btnAnalytics);

            if(!sharedPreferences.getBoolean("firebase_analytics.enable", false)){
                btnAnalytics.setEnabled(true);
                btnAnalytics.setBackground(active);
                btnAnalytics.setOnClickListener(view -> {
                    editor.putBoolean("firebase_analytics.enable", true);
                    editor.apply();
                    btnAnalytics.setBackground(inActive);
                    btnAnalytics.setEnabled(false);
                });
            }

        }

        if(sharedPreferences.getBoolean("firebase_analytics.enable", false)){
            if(position == 2) {
                btnCrashlytics = findViewById(R.id.btnCrashlytics);

                if(!sharedPreferences.getBoolean("firebase_crashlytics.enable", true)){
                    btnCrashlytics.setEnabled(true);
                    btnCrashlytics.setBackground(active);
                    btnCrashlytics.setOnClickListener(view -> {
                        editor.putBoolean("firebase_crashlytics.enable", true);
                        editor.apply();
                        btnCrashlytics.setBackground(inActive);
                        btnCrashlytics.setEnabled(false);
                    });
                }
            }
        }

        if(position == 3) {
            btnLogin = findViewById(R.id.btnLogin);

            Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType(getString(R.string.auth_type));

            if(accounts.length > 0) {
                btnLogin.setEnabled(false);
            }
            else {
                btnLogin.setEnabled(true);
                btnLogin.setOnClickListener(view ->
                        openLogin());
            }
            if(accounts.length > 0) {
                btnLogin.setEnabled(false);
            }
        }
    }

    void openLogin(){
        Intent intent = new Intent(this, AuthenticatorActivity.class);
        startActivity(intent);
    }

    @Override
    public void finishTutorial() {
        editor.putBoolean("FIRST_RUN", false);

        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
