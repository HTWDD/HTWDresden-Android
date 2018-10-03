package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Adapter für die Mensa Tagesübersicht
 *
 * @author Kay Förster
 */
public class MensaOverviewDayShortAdapter extends RealmBaseAdapter<Meal> {

    public MensaOverviewDayShortAdapter(@Nullable final OrderedRealmCollection<Meal> data) {
        super(data);
    }

    @Override
    public View getView(final int i, @Nullable View view, final ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.mensa_list_item_simple, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.title = view.findViewById(R.id.value);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Meal meal = getItem(i);
        if (meal == null) {
            return view;
        }

        viewHolder.title.setText(meal.getTitle());

        return view;
    }

    private static class ViewHolder {
        TextView title;
    }
}
