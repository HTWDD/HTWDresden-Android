package de.htwdd.htwdresden.adapter;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.htwdd.htwdresden.interfaces.ISpinnerEntity;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;

/**
 * Adapter um Daten in einem Spinner anzeigen zu können
 *
 * @author Kay Förster
 */

public class SpinnerAdapter<T extends RealmObject & ISpinnerEntity> extends RealmBaseAdapter<T> {
    private final boolean addInitialText;
    private final String initialText;

    public SpinnerAdapter(@Nullable OrderedRealmCollection<T> data, @Nullable final String initialText) {
        super(data);
        this.addInitialText = true;
        this.initialText = initialText;
    }

    @Override
    public boolean isEnabled(final int position) {
        return !(addInitialText && position == 0) && super.isEnabled(position);
    }

    @Override
    public View getView(final int i, View view, final ViewGroup parent) {
        final ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view;
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Initial Text oder Eintrag anzeigen
        if (addInitialText && i == 0) {
            viewHolder.name.setText(initialText);
            viewHolder.name.setEnabled(false);
        } else {
            final ISpinnerEntity spinnerName = getItem(i);
            if (spinnerName != null) {
                viewHolder.name.setText(spinnerName.getSpinnerName());
            }
            viewHolder.name.setEnabled(true);
        }

        return view;
    }

    @Override
    public int getCount() {
        final int count = super.getCount();
        return !addInitialText || initialText == null ? count : count + 1;
    }

    @Nullable
    @Override
    public T getItem(final int position) {
        int mPosition = position;
        if (addInitialText)
            mPosition--;
        if (addInitialText && position == 0)
            return null;

        return super.getItem(mPosition);
    }

    private static class ViewHolder {
        TextView name;
    }
}
