package de.htwdd.htwdresden.account.ListAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import de.htwdd.htwdresden.R;

public class ListAdapter extends ArrayAdapter<Item> {
	private List<Item> appsList = null;
	private Context context;

	public ListAdapter(Context context, int textViewResourceId, List<Item> appsList) {
		super(context, textViewResourceId, appsList);
		this.context = context;
		this.appsList = appsList;
	}

	@Override
	public int getCount() {
		return ((null != appsList) ? appsList.size() : 0);
	}

	@Override
	public Item getItem(int position) {
		return ((null != appsList) ? appsList.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (null == view) {
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (layoutInflater != null) {
				view = layoutInflater.inflate(R.layout.row_layout, parent, false);
			}
		}

		final Item data = appsList.get(position);
		if (null != data) {

			TextView appName = null;
			if (view != null) {
				appName = view.findViewById(R.id.key);
				TextView packageName = view.findViewById(R.id.value);
				Button b = view.findViewById(R.id.button_remove);

			b.setTag(position);
			b.setOnClickListener(v -> {
				AccountManager mAccountManager;
				mAccountManager = AccountManager.get(context);
				Account account = new Account(data.getValue(), context.getString(R.string.auth_type));
				mAccountManager.removeAccount(account, arg0 -> {
					if (arg0.isDone())
						Toast.makeText(context, "Removed " + data.getValue(), Toast.LENGTH_SHORT).show();
					appsList.remove(Integer.parseInt(String.valueOf(v.getTag())));
					notifyDataSetChanged();
				}, null);
			});
				appName.setText(data.getKey());
				packageName.setText(data.getValue());
			}
		}
		return view;
	}
}
