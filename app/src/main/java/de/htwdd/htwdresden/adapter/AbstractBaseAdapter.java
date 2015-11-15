package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Standard Grundgerüst für Listen
 *
 * @author Kay Förster
 */
public abstract class AbstractBaseAdapter<T> extends BaseAdapter {
    ArrayList<T> data;
    LayoutInflater mLayoutInflater;
    Context context;

    public AbstractBaseAdapter(Context context, ArrayList<T> data) {
        this.data = data;
        this.context = context;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
