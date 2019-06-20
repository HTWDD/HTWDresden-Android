package de.htwdd.htwdresden.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.adapter.SpinnerAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.studyGroups.StudyCourse;
import de.htwdd.htwdresden.types.studyGroups.StudyGroup;
import de.htwdd.htwdresden.types.studyGroups.StudyYear;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.Credentials;

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

	public Account findAccount(String accountName) {
		for (Account account : mAccountManager.getAccounts())
			if (TextUtils.equals(account.name, accountName) && TextUtils.equals(account.type, getString(R.string.auth_type))) {
				System.out.println("FOUND");
				return account;
			}
		return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);

		Log.d(TAG, "onCreate");

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

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.jahrgang_werte, android.R.layout.simple_spinner_dropdown_item);
		yearSpinner.setAdapter(adapter);

		groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
				if (i != 0) {
					// Wenn "Bitte auswählen" ausgewählt ist, sind keine Daten für nachfolgende Spinner verfügbar.
					studyGroup = ((StudyGroup) adapterView.getAdapter().getItem(i)).getStudyGroup();
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
					groupSpinner.setAdapter(new SpinnerAdapter<>(null, pleaseSelectString));
					studyGroup = null;
					return;
				}
				// Auswahl merken
				final StudyCourse studyCourseObject = (StudyCourse) adapterView.getAdapter().getItem(i);
				studyCourse = studyCourseObject.getStudyCourse();

				// Auswahl selektieren
				int position = 0;
				final RealmList<StudyGroup> studyGroups = studyCourseObject.getStudyGroups();
				if (sharedPreferences.contains(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGRUPPE)) {
					position = 1 + studyGroups.indexOf(studyGroups
							.where()
							.equalTo(Const.database.StudyGroups.STUDY_GROUP, sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGRUPPE, ""))
							.findFirst()
					);
				}
				groupSpinner.setAdapter(new SpinnerAdapter<>(((StudyCourse) adapterView.getAdapter().getItem(i)).getStudyGroups(), pleaseSelectString));
				groupSpinner.setSelection(position);
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
				// Auswahl merken
				final StudyYear studyYearObject = (StudyYear) adapterView.getAdapter().getItem(i);
				studyYear = studyYearObject.getStudyYear();

				// Nachfolgenden Spinner füllen und Auswahl selektieren
				int position = 0;
				final RealmList<StudyCourse> studyCourses = studyYearObject.getStudyCourses();
				// Finde ausgewählte Position
				if (sharedPreferences.contains(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGANG)) {
					position = 1 + studyCourses.indexOf(studyCourses
							.where()
							.equalTo(Const.database.StudyGroups.STUDY_COURSE, sharedPreferences.getString(Const.preferencesKey.PREFERENCES_TIMETABLE_STUDIENGANG, ""))
							.findFirst()
					);
				}
				courseSpinner.setAdapter(new SpinnerAdapter<>(studyCourses, pleaseSelectString));
				courseSpinner.setSelection(position);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> adapterView) {
			}
		});
		yearSpinner.setSelection(yearPosition);

		// If this is a first time adding, then this will be null
		accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
		mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);

		if (mAuthTokenType == null)
			mAuthTokenType = getString(R.string.auth_type);

		findAccount(accountName);

		System.out.println(mAuthTokenType + ", accountName : " + accountName);

		findViewById(R.id.submit).setOnClickListener(this);
	}

	void userSignIn() {

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
			userData.putString("StgGrp", studyGroup);
			userData.putString("Stg", studyCourse);
			userData.putString("studyGroupYear", studyYear + "");
			data.putBundle(AccountManager.KEY_USERDATA, userData);

			//Make it an intent to be passed back to the Android Authenticator
			final Intent res = new Intent();
			res.putExtras(data);

			//Create the new account with Account Name and TYPE
			final Account account = new Account(accountName, mAuthTokenType);

			//Add the account to the Android System
			if (mAccountManager.addAccountExplicitly(account, password, userData)) {
				try {
					Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType(getString(R.string.auth_type));

					if(accounts.length > 1) {
						mAccountManager.removeAccount(accounts[0], arg0 -> {
						}, null);
					}
				} catch (Exception e) {
					Log.i(TAG, "Exception:" + e);
				}
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
	}

	@Override
	public void onClick(View v) {
		userSignIn();		
	}
}
