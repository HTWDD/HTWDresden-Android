package de.htwdd.htwdresden;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

import de.htwdd.htwdresden.adapter.SpinnerAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.studyGroups.StudyCourse;
import de.htwdd.htwdresden.types.studyGroups.StudyData;
import de.htwdd.htwdresden.types.studyGroups.StudyGroup;
import de.htwdd.htwdresden.types.studyGroups.StudyYear;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static de.htwdd.htwdresden.account.AuthenticatorActivity.PARAM_USER_PASS;

public class OnBoardActivity extends TutorialActivity {

    private int studyYear;
    private String studyCourse;
    private String studyGroup;

    private AccountManager mAccountManager;
    private String mAuthTokenType;
    String authtoken = "123456789"; // this
    String password = "12345";
    String accountName;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LottieAnimationView ltAnimViewActiveAnalytics;
    LottieAnimationView ltAnimViewInActiveAnalytics;
    LottieAnimationView animationCheckBox;
    TextView noteCrashlytics;
    Button buttonPrev;
    Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setIndicator(R.drawable.indicator_selected_inactive);
        setIndicatorSelected(R.drawable.indicator_selected);
        int i = 1;

        mAccountManager = AccountManager.get(getBaseContext());
        mAuthTokenType = getString(R.string.auth_type);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

//        if(sharedPreferences.getBoolean("FIRST_RUN", true)){
        if(i!=1){
            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_welcome)
                    .setTitle(getString(R.string.welcome))
                    .setContent(getString(R.string.welcome_text_content))
                    .setDrawable(R.drawable.htw_logo_gross)
                    .build());
            // Permission Step
            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_analytics)
                    .setTitle(getString(R.string.analytics))
                    .setContent(getString(R.string.analytics_text_content))
                    .setSummary(getString(R.string.analytics_text_summary))
                    .setDrawable(R.drawable.htw_logo_gross)
                    .build());

            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_crashlogger)
                    .setTitle(getString(R.string.crashlytics))
                    .setContent(getString(R.string.crashlytics_text_content))
                    .setSummary(getString(R.string.crashlytics_text_summary))
                    .setDrawable(R.drawable.htw_logo_gross)
                    .build());

            addFragment(new Step.Builder()
                    .setTitle(getString(R.string.settings_studiengruppe))
                    .setView(R.layout.onboarding_fragment_studyyear)
                    .setContent(getString(R.string.studygroup_text_content))
                    .setDrawable(R.drawable.htw_logo_gross)
                    .setSummary(getString(R.string.studygroup_text_summary))
                    .build());

            addFragment(new Step.Builder()
                    .setTitle(getString(R.string.login_with_img))
                    .setView(R.layout.onboarding_fragment_login)
                    .setContent(getString(R.string.login_text_content))
                    .setDrawable(R.drawable.htw_logo_gross)
                    .setSummary(getString(R.string.login_text_summary))
                    .build());

            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_finish_tutorial)
                    .setTitle(getString(R.string.finish_text_header))
                    .setContent(getString(R.string.finish_text_content))
                    .setSummary(getString(R.string.finish_text_summary))
                    .setDrawable(R.drawable.htw_logo_gross)
                    .build());

            buttonPrev = findViewById(R.id.prev);
            buttonPrev.setTextColor(getResources().getColor(R.color.middle_gray));
            buttonPrev.setVisibility(View.GONE);
            buttonNext = findViewById(R.id.next);
            buttonNext.setTextColor(getResources().getColor(R.color.middle_gray));
            setPrevText("Zurück");
            setNextText("Weiter");
            setFinishText("Los geht's!");
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
            if(sharedPreferences.getBoolean("firebase_analytics.enable", false)){

                animationCheckBox = findViewById(R.id.anim_active_crashlytics);
                noteCrashlytics = findViewById(R.id.notes_crashlytics);
                noteCrashlytics.setVisibility(View.GONE);

                if(!sharedPreferences.getBoolean("firebase_crashlytics.enable", false)){
                    animationCheckBox.setEnabled(true);
                    animationCheckBox.setAnimation("success_blue.json");
                    animationCheckBox.setRepeatCount(0);
                    animationCheckBox.playAnimation();
                    animationCheckBox.setOnClickListener(view -> {
                        editor.putBoolean("firebase_crashlytics.enable", true);
                        editor.apply();
                        animationCheckBox.setAnimation("check_mark_success_blue.json");
                        animationCheckBox.setRepeatCount(0);
                        animationCheckBox.playAnimation();
                        animationCheckBox.setEnabled(false);
                    });
                }
            }
            else {
                noteCrashlytics = findViewById(R.id.notes_crashlytics);
                noteCrashlytics.setVisibility(View.VISIBLE);
            }
        }

        if(position == 3) {
            prepareSpinners();
        }

        if(position == 4) {
            Button login = findViewById(R.id.btnLogin);

            login.setOnClickListener(view -> userSignIn());
        }
    }

    void prepareSpinners() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<StudyYear> realmResultsYear = realm.where(StudyYear.class).findAll();

        // Finde aktuell ausgewählte Position
        int yearPosition = 0;
        if (sharedPreferences.contains(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR)) {
            yearPosition = 1 + realmResultsYear.indexOf(realm.where(StudyYear.class)
                    .equalTo(Const.database.StudyGroups.STUDY_YEAR, sharedPreferences.getInt(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENJAHR, 18))
                    .findFirst()
            );
        }

        final String pleaseSelectString = getString(R.string.spinner_select_option);
        Spinner yearSpinner = findViewById(R.id.ctvJahrgang);
        Spinner courseSpinner = findViewById(R.id.ctvStudiengang);
        Spinner groupSpinner = findViewById(R.id.ctvStudiengruppe);

        courseSpinner.setEnabled(false);
        groupSpinner.setEnabled(false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.jahrgang_werte, android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                if (i != 0) {
                    // Wenn "Bitte auswählen" ausgewählt ist, sind keine Daten für nachfolgende Spinner verfügbar.
                    studyGroup = ((StudyGroup) adapterView.getAdapter().getItem(i)).getStudyGroup();

                    StudyData studyData = new StudyData();
                    studyData.setId(123);
                    studyData.setStudyYear(studyYear);
                    studyData.setStudyCourse(studyCourse);
                    studyData.setStudyGroup(studyGroup);

                    final Realm realmStudyData = Realm.getDefaultInstance();
                    realmStudyData.beginTransaction();
                    realmStudyData.where(StudyData.class).findAll().deleteAllFromRealm();
                    realmStudyData.copyToRealmOrUpdate(studyData);
                    realmStudyData.commitTransaction();
                    realmStudyData.close();

                    buttonNext.callOnClick();
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                if (i == 0) {
                    // Wenn "Bitte auswählen" ausgewählt ist, sind keine Daten für nachfolgende Spinner verfügbar.
                    groupSpinner.setAdapter(new SpinnerAdapter<>(null, "Studiengruppe " + pleaseSelectString));
                    studyGroup = null;
                    return;
                }
                // Auswahl merken
                final StudyCourse studyCourseObject = (StudyCourse) adapterView.getAdapter().getItem(i);
                studyCourse = studyCourseObject.getStudyCourse();

                // Auswahl selektieren
                groupSpinner.setAdapter(new SpinnerAdapter<>(((StudyCourse) adapterView.getAdapter().getItem(i)).getStudyGroups(), "Studiengruppe " + pleaseSelectString));
                groupSpinner.setEnabled(true);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });
        yearSpinner.setAdapter(new SpinnerAdapter<>(realmResultsYear, "Jahrgang " + pleaseSelectString));
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                if (i == 0) {
                    // Wenn "Bitte auswählen" ausgewählt ist, sind keine Daten für nachfolgende Spinner verfügbar.
                    courseSpinner.setAdapter(new SpinnerAdapter<>(null, "Studienkurs " + pleaseSelectString));
                    studyYear = 0;

                    return;
                }
                // Auswahl merken
                final StudyYear studyYearObject = (StudyYear) adapterView.getAdapter().getItem(i);
                studyYear = studyYearObject.getStudyYear();

                // Nachfolgenden Spinner füllen und Auswahl selektieren
                final RealmList<StudyCourse> studyCourses = studyYearObject.getStudyCourses();

                courseSpinner.setAdapter(new SpinnerAdapter<>(studyCourses, "Studienkurs " + pleaseSelectString));
                courseSpinner.setSelection(0);
                courseSpinner.setEnabled(true);
                groupSpinner.setEnabled(false);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });
        yearSpinner.setSelection(yearPosition);
        groupSpinner.setSelection(0);
        courseSpinner.setSelection(0);
    }

    @Override
    public void finishTutorial() {
        editor.putBoolean("FIRST_RUN", false);

        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    void userSignIn() {

        //Alten Nutzer entfernen
        try {
            Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType(getString(R.string.auth_type));

            if(accounts.length > 0) {
                mAccountManager.removeAccount(accounts[0], arg0 -> {
                }, null);
            }
            Realm.getDefaultInstance().deleteAll();
        } catch (Exception e) {
            String error = e.toString();
        }

        // You should probably call your server with user credentials and get
        // the authentication token here.
        // For demo, I have hard-coded it.
        authtoken = "123456789";

        accountName = ((EditText) findViewById(R.id.myNumber)).getText().toString().trim();
        password = ((EditText) findViewById(R.id.rzLogin)).getText().toString().trim();

        if (accountName.length() > 0) {
            Bundle data = new Bundle();
            data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
            data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAuthTokenType);
            data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
            data.putString(PARAM_USER_PASS, password);

            // Some extra data about the user
            Bundle userData = new Bundle();
            userData.putString("RZLogin", password);
            data.putBundle(AccountManager.KEY_USERDATA, userData);

            //Make it an intent to be passed back to the Android Authenticator
            final Intent res = new Intent();
            res.putExtras(data);

            //Create the new account with Account Name and TYPE
            final Account account = new Account(accountName, mAuthTokenType);

            //Add the account to the Android System
            if (mAccountManager.addAccountExplicitly(account, password, userData)) {
                // worked
                mAccountManager.setAuthToken(account, mAuthTokenType, authtoken);
                //setAccountAuthenticatorResult(data);
                setResult(RESULT_OK, res);

                Button btnLogin = findViewById(R.id.btnLogin);
                btnLogin.setEnabled(false);
                btnLogin.setBackground(getResources().getDrawable(R.drawable.more_rounded_corners_background_gray));
                btnLogin.setTextColor(Color.BLACK);

                EditText sNummer = findViewById(R.id.myNumber);
                EditText pswd = findViewById(R.id.rzLogin);

                sNummer.setEnabled(false);
                pswd.setEnabled(false);

                buttonNext.callOnClick();

            } else {
                // guess not
                String error = "Account not added";
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Bitte überprüfe, ob alle erforderlichen Daten angegeben wurden", Toast.LENGTH_SHORT).show();
        }
    }
}
