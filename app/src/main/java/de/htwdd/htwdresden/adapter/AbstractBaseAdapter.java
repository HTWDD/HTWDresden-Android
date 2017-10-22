package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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
abstract class AbstractBaseAdapter<T> extends BaseAdapter {
    final ArrayList<T> data;
    final LayoutInflater mLayoutInflater;
    final Context context;

    AbstractBaseAdapter(@NonNull final Context context, @NonNull final ArrayList<T> data) {
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
