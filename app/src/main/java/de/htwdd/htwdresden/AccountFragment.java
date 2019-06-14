package de.htwdd.htwdresden;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import de.htwdd.htwdresden.account.AccountActivity;
import de.htwdd.htwdresden.account.AuthenticatorActivity;
import de.htwdd.htwdresden.account.ListAdapter.Item;
import de.htwdd.htwdresden.account.ListAdapter.ListAdapter;

public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    private String TAG = this.getClass().getSimpleName();
    @SuppressWarnings("rawtypes")
    private List list = null;
    private ListView listView;
    private ListAdapter listadaptor;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.account_activity, container, false);

        // Setze OnClickListener für Button
        view.findViewById(R.id.button1).setOnClickListener(arg0 -> {
            Intent createIntent = new Intent(getContext(), AuthenticatorActivity.class);
            startActivity(createIntent);
        });

        // Setze OnClickListener für Button
        view.findViewById(R.id.button2).setOnClickListener(arg0 -> {
            list = getData();
            listView = view.findViewById(R.id.listView1);
            listadaptor = new ListAdapter(getContext(), R.layout.row_layout, list);
            listView.setAdapter(listadaptor);
        });

        return view;
    }

    private ArrayList<Item> getData() {
        ArrayList<Item> accountsList = new ArrayList<Item>();

        AccountActivity accAct = new AccountActivity();
        // Getting all registered Our Application Accounts;
        try {
            Account[] accounts = AccountManager.get(getContext()).getAccountsByType(getString(R.string.auth_type));
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
}
