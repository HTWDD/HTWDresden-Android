package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.interfaces.ISpinnerName;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;


/**
 * Adapter um Daten in einem Spinner anzeigen zu können
 *
 * @author Kay Förster
 */
public class SpinnerRealmAdapter<T extends RealmObject & ISpinnerName> extends RealmBaseAdapter<T> {
    private final boolean addInitialText;
    private final String initialText;

    public SpinnerRealmAdapter(@NonNull final Context context, @Nullable final OrderedRealmCollection<T> data) {
        super(context, data);
        addInitialText = false;
        initialText = null;
    }

    public SpinnerRealmAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<T> data, @Nullable final String initialText) {
        super(context, data);
        this.addInitialText = true;
        this.initialText = initialText;
    }

    @Override
    public boolean isEnabled(final int position) {
        return !(addInitialText && position == 0) && super.isEnabled(position);
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_item, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.spinner_text);
            view.setTag(viewHolder);
        } else viewHolder = (ViewHolder) view.getTag();

        if (addInitialText && i == 0) {
            viewHolder.name.setText(initialText);
            viewHolder.name.setEnabled(false);
            viewHolder.name.setSelected(false);
        } else {
            final ISpinnerName spinnerName = getItem(i);
            if (spinnerName != null)
                viewHolder.name.setText(spinnerName.getSpinnerSelect());
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
