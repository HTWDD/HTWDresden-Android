package de.htwdd.htwdresden;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import de.htwdd.htwdresden.account.AuthenticatorActivity;
import de.htwdd.htwdresden.account.ListAdapter.Item;

public class AccountFragment extends Fragment {

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

        try {
            Account[] accounts = AccountManager.get(getContext()).getAccountsByType(getString(R.string.auth_type));
            for (Account account : accounts) {
                Item item = new Item(account.type, account.name);
                accountsList.add(item);
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception:" + e);
        }

        return accountsList;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        list = getData();
        textView = view.findViewById(R.id.account_tv);

        if(list.isEmpty())
        {
            button.setText(getString(R.string.sign_in));
            textView.setText(getString(R.string.not_logged_in_text));
        }
        else {
            button.setText(getString(R.string.change_user));
            textView.setText(getString(R.string.logged_in_as) + list.get(0).getValue() + " " + getString(R.string.logged_in_as_test2));
        }
    }
}
