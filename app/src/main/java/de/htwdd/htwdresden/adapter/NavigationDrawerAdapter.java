package de.htwdd.htwdresden.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.NavigationDrawerItem;

public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {
    private LayoutInflater layoutInflater;
    private int layoutResID;
    private List<NavigationDrawerItem> navigationDrawerItems;

    public NavigationDrawerAdapter(Context context, int resource, List<NavigationDrawerItem> listItems) {
        super(context, resource, listItems);

        layoutInflater = ((Activity) context).getLayoutInflater();
        layoutResID = resource;
        navigationDrawerItems = listItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // inflate the layout
            convertView = layoutInflater.inflate(layoutResID, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.itemTitle);
            viewHolder.headerName = (TextView) convertView.findViewById(R.id.headerName);
            viewHolder.headerLayout = (LinearLayout) convertView.findViewById(R.id.headerLayout);
            viewHolder.itemLayout = (LinearLayout) convertView.findViewById(R.id.itemLayout);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        // Hole Item
        NavigationDrawerItem drawerItem = navigationDrawerItems.get(position);

        if (drawerItem.getImgResID() == 0) {
            viewHolder.itemLayout.setVisibility(View.GONE);
            viewHolder.headerLayout.setVisibility(View.VISIBLE);
            viewHolder.headerName.setText(drawerItem.getItemName());
        } else {
            viewHolder.itemLayout.setVisibility(View.VISIBLE);
            viewHolder.headerLayout.setVisibility(View.GONE);
            viewHolder.itemName.setText(drawerItem.getItemName());
            viewHolder.itemName.setCompoundDrawablesWithIntrinsicBounds(drawerItem.getImgResID(), 0, 0, 0);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView itemName;
        TextView headerName;
        LinearLayout itemLayout;
        LinearLayout headerLayout;
    }
}