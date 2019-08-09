package de.htwdd.htwdresden.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.adapter.SpinnerAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.studyGroups.StudyCourse;
import de.htwdd.htwdresden.types.studyGroups.StudyData;
import de.htwdd.htwdresden.types.studyGroups.StudyGroup;
import de.htwdd.htwdresden.types.studyGroups.StudyYear;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements OnClickListener {

	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
	public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

	public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

	public final static String PARAM_USER_PASS = "USER_PASS";

	private int studyYear;
	private String studyCourse;
	private String studyGroup;

	private final String TAG = this.getClass().getSimpleName();

	private AccountManager mAccountManager;
	private String mAuthTokenType;
	String authtoken = "123456789"; // this
	String password = "12345";

	String accountName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);

		Log.d(TAG, "onCreate");

		studyCourse = null;
		studyGroup = null;

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

		mAccountManager = AccountManager.get(getBaseContext());

		final String pleaseSelectString = getString(R.string.general_select_option);
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
				}
				hideKeyboardFrom(view);
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
					groupSpinner.setAdapter(new SpinnerAdapter<>(null, pleaseSelectString));
					studyGroup = null;
					return;
				}
				hideKeyboardFrom(view);
				// Auswahl merken
				final StudyCourse studyCourseObject = (StudyCourse) adapterView.getAdapter().getItem(i);
				studyCourse = studyCourseObject.getStudyCourse();

				// Auswahl selektieren
				groupSpinner.setAdapter(new SpinnerAdapter<>(((StudyCourse) adapterView.getAdapter().getItem(i)).getStudyGroups(), pleaseSelectString));
				groupSpinner.setEnabled(true);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> adapterView) {
			}
		});
		yearSpinner.setAdapter(new SpinnerAdapter<>(realmResultsYear, pleaseSelectString));
		yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
				if (i == 0) {
					// Wenn "Bitte auswählen" ausgewählt ist, sind keine Daten für nachfolgende Spinner verfügbar.
					courseSpinner.setAdapter(new SpinnerAdapter<>(null, pleaseSelectString));
					studyYear = 0;

					return;
				}
				hideKeyboardFrom(view);
				// Auswahl merken
				final StudyYear studyYearObject = (StudyYear) adapterView.getAdapter().getItem(i);
				studyYear = studyYearObject.getStudyYear();

				// Nachfolgenden Spinner füllen und Auswahl selektieren
				final RealmList<StudyCourse> studyCourses = studyYearObject.getStudyCourses();

				courseSpinner.setAdapter(new SpinnerAdapter<>(studyCourses, pleaseSelectString));
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

		mAuthTokenType = getString(R.string.auth_type);

		findViewById(R.id.submit).setOnClickListener(this);
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
			Log.i(TAG, "Exception:" + e);
		}

		// You should probably call your server with user credentials and get
		// the authentication token here.
		// For demo, I have hard-coded it.
		authtoken = "123456789";

		accountName = ((EditText) findViewById(R.id.accountName)).getText().toString().trim();
		password = ((EditText) findViewById(R.id.accountPassword)).getText().toString().trim();

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
				Log.d(TAG, "Account added");
				mAccountManager.setAuthToken(account, mAuthTokenType, authtoken);
				setAccountAuthenticatorResult(data);
				setResult(RESULT_OK, res);
				finish();
			} else {
				// guess not
				Log.d(TAG, "Account NOT added");
			}
		}
		else{
			Toast.makeText(getApplicationContext(), "Bitte überprüfe, ob alle erforderlichen Daten angegeben wurden", Toast.LENGTH_SHORT).show();
		}
	}

	public void hideKeyboardFrom(View view) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		assert imm != null;
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	public void onClick(View v) {
		userSignIn();		
	}
}
