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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
    private ArrayList<Item> list = null;
    private TextView textView;
    private View view;
    private Button button;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.account_activity, container, false);
        button = view.findViewById(R.id.button1);

        // Setze OnClickListener für Button
        view.findViewById(R.id.button1).setOnClickListener(arg0 -> {
            Intent createIntent = new Intent(getContext(), AuthenticatorActivity.class);
            startActivity(createIntent);
        });

        return view;
    }

    private ArrayList<Item> getData() {
        ArrayList<Item> accountsList = new ArrayList<Item>();

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

    @Override
    public void onStart() {
        super.onStart();

        list = getData();
        textView = view.findViewById(R.id.account_tv);

        if(list.isEmpty())
        {
            button.setText("Login");
            textView.setText("Sie sind mit keinem Account eingeloggt. Um die volle Funktionalität nutzen zu können, loggen Sie sich bitte ein.");
        }
        else {
            button.setText("Nutzer wechseln");
            textView.setText("Sie sind eingeloggt als: " + list.get(0).getValue() + "\nSollte dies nicht Ihr Account sein, nutzen Sie bitte die \"Nutzer wechseln\"-Schaltfläche unten, um sich mit Ihrem Account einzuloggen.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            Thread.sleep(1200);
            list = getData();
            textView = view.findViewById(R.id.account_tv);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(list.isEmpty())
        {
            button.setText("Login");
            textView.setText("Sie sind mit keinem Account eingeloggt. Um die volle Funktionalität nutzen zu können, loggen Sie sich bitte ein.");
        }
        else {
            button.setText("Nutzer wechseln");
            textView.setText("Sie sind eingeloggt als: " + list.get(0).getValue() + "\nSollte dies nicht Ihr Account sein, nutzen Sie bitte die \"Nutzer wechseln\"-Schaltfläche unten, um sich mit Ihrem Account einzuloggen.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        list = getData();
        textView = view.findViewById(R.id.account_tv);

        if(list.isEmpty())
        {
            button.setText("Login");
            textView.setText("Sie sind mit keinem Account eingeloggt. Um die volle Funktionalität nutzen zu können, loggen Sie sich bitte ein.");
        }
        else {
            button.setText("Nutzer wechseln");
            textView.setText("Sie sind eingeloggt als: " + list.get(0).getValue() + "\nSollte dies nicht Ihr Account sein, nutzen Sie bitte die \"Nutzer wechseln\"-Schaltfläche unten, um sich mit Ihrem Account einzuloggen.");
        }
    }
}
