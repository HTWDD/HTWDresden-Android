package de.htwdd.htwdresden.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.account.ListAdapter.Item;

public class AccountActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);


        findViewById(R.id.button1).setOnClickListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button1) {
            Intent createIntent = new Intent(AccountActivity.this, AuthenticatorActivity.class);
            startActivity(createIntent);
        }
    }
}
