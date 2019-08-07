package de.htwdd.htwdresden;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

import de.htwdd.htwdresden.account.AuthenticatorActivity;

public class OnBoardActivity extends TutorialActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LottieAnimationView ltAnimViewActiveAnalytics;
    LottieAnimationView ltAnimViewInActiveAnalytics;
    LottieAnimationView animationCheckBox;
    LottieAnimationView animationGreenButton;
    LottieAnimationView animationRedButton;
    TextView noteCrashlytics;
    Button buttonPrev;
    Button buttonNext;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            ltAnimViewActiveAnalytics = findViewById(R.id.anim_active_analytics);
            ltAnimViewInActiveAnalytics = findViewById(R.id.anim_not_active_analytics);

            if(!sharedPreferences.getBoolean("firebase_analytics.enable", false)){
                ltAnimViewInActiveAnalytics.setEnabled(true);
                ltAnimViewInActiveAnalytics.setOnClickListener(view -> {
                    editor = sharedPreferences.edit();
                    editor.putBoolean("firebase_analytics.enable", true);
                    editor.apply();
                    ltAnimViewInActiveAnalytics.setVisibility(View.GONE);
                    ltAnimViewActiveAnalytics.setVisibility(View.VISIBLE);
                    ltAnimViewActiveAnalytics.playAnimation();
                });
            }
            else {
                ltAnimViewInActiveAnalytics.setVisibility(View.GONE);
                ltAnimViewActiveAnalytics.setVisibility(View.VISIBLE);
            }
        }

        if(position == 2) {
            animationCheckBox = findViewById(R.id.anim_active_crashlytics);
            animationGreenButton = findViewById(R.id.anim_not_active_crashlytics);
            animationRedButton = findViewById(R.id.anim_inactive_crashlytics);
            noteCrashlytics = findViewById(R.id.notes_crashlytics);

            if(sharedPreferences.getBoolean("firebase_analytics.enable", false)) {
                animationCheckBox.setVisibility(View.VISIBLE);
                animationCheckBox.setEnabled(true);
                animationRedButton.setVisibility(View.GONE);
                noteCrashlytics.setVisibility(View.GONE);
                animationCheckBox.setOnClickListener(view -> {
                    editor = sharedPreferences.edit();
                    editor.putBoolean("firebase_crashlytics.enable", true);
                    editor.apply();
                }
                );
                if (!sharedPreferences.getBoolean("firebase_crashlytics.enable", true)) {
                    animationCheckBox.setEnabled(true);
                    animationCheckBox.setVisibility(View.VISIBLE);
                    animationGreenButton.setVisibility(View.GONE);
                }
                else {
                    animationCheckBox.setEnabled(false);
                    animationCheckBox.setVisibility(View.GONE);
                    animationGreenButton.setVisibility(View.VISIBLE);
                    animationGreenButton.playAnimation();
                }
            }
            else {
                animationRedButton.setVisibility(View.VISIBLE);
                noteCrashlytics.setVisibility(View.VISIBLE);
            }
        }


//        if(position == 2) {
//            animationCheckBox = findViewById(R.id.anim_active_crashlytics);
//            animationGreenButton = findViewById(R.id.anim_not_active_crashlytics);
//            animationRedButton = findViewById(R.id.anim_inactive_crashlytics);
//            noteCrashlytics = findViewById(R.id.notes_crashlytics);
//
//            if(sharedPreferences.getBoolean("firebase_analytics.enable", false)){
//
//                animationGreenButton.setVisibility(View.VISIBLE);
//                noteCrashlytics.setVisibility(View.GONE);
//                animationRedButton.setVisibility(View.GONE);
//
//                if(!sharedPreferences.getBoolean("firebase_crashlytics.enable", true)){
//
//                    animationCheckBox.setVisibility(View.GONE);
//                    animationGreenButton.setVisibility(View.VISIBLE);
//
//                    animationGreenButton.setOnClickListener(view -> {
//                        editor.putBoolean("firebase_crashlytics.enable", true);
//                        editor.apply();
//                        animationGreenButton.setVisibility(View.GONE);
//                        animationGreenButton.setEnabled(false);
//                        animationCheckBox.setVisibility(View.VISIBLE);
//                        animationCheckBox.playAnimation();
//                    });
//                }
//                else {
//                    animationGreenButton.setVisibility(View.GONE);
//                    animationCheckBox.setVisibility(View.VISIBLE);
//                }
//            }
//        }

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
