package de.htwdd.htwdresden.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.account.ListAdapter.Item;
import de.htwdd.htwdresden.account.ListAdapter.ListAdapter;

public class AccountActivity extends Activity implements OnClickListener {

    private String TAG = this.getClass().getSimpleName();
    private AccountManager mAccountManager;
    @SuppressWarnings("rawtypes")
    private List list = null;
    private ListView listView;
    private ListAdapter listadaptor;
    public static final String DEMO_ACCOUNT_NAME = "Demo Account";
    public static final String DEMO_ACCOUNT_PASSWORD = "Demo123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);

        mAccountManager = AccountManager.get(this);

        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
    }

//    private void showMessage(final String msg) {
//        if (TextUtils.isEmpty(msg))
//            return;
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private ArrayList<Item> getData() {
        ArrayList<Item> accountsList = new ArrayList<Item>();

        // Getting all registered Our Application Accounts;
        try {
            Account[] accounts = AccountManager.get(this).getAccountsByType(getString(R.string.auth_type));
            for (Account account : accounts) {
                Item item = new Item(account.type, account.name);
                accountsList.add(item);
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e);
        }

        // For all registered accounts;
        /*
         * try { Account[] accounts = AccountManager.get(this).getAccounts();
         * for (Account account : accounts) { Item item = new Item(
         * account.type, account.name); accountsList.add(item); } } catch
         * (Exception e) { Log.i("Exception", "Exception:" + e); }
         */
        return accountsList;
    }

//    void createDemoAccount() {
//        Account account = new Account(DEMO_ACCOUNT_NAME, getString(R.string.auth_type));
//        boolean accountCreated = mAccountManager.addAccountExplicitly(account, DEMO_ACCOUNT_PASSWORD, null);
//        if (accountCreated) {
//            showMessage("Account Created");
//        }
//    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button1) {
            Intent createIntent = new Intent(AccountActivity.this, AuthenticatorActivity.class);
            startActivity(createIntent);
        }
        if (v.getId() == R.id.button2) {
            list = getData();
            listView = findViewById(R.id.listView1);
            listadaptor = new ListAdapter(AccountActivity.this, R.layout.row_layout, list);
            listView.setAdapter(listadaptor);
        }
    }
}
