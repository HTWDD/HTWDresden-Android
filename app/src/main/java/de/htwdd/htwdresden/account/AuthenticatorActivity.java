package de.htwdd.htwdresden.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.account.ListAdapter.Item;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements OnClickListener{

	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
	public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

	public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

	public final static String PARAM_USER_PASS = "USER_PASS";

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

		mAccountManager = AccountManager.get(getBaseContext());

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
			userData.putString("UserID", "25");
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
