package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.types.Meal;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Adapter für die Mensa Tagesübersicht
 *
 * @author Kay Förster
 */
public class MensaOverviewDayAdapter extends RealmBaseAdapter<Meal> {
    public MensaOverviewDayAdapter(@NonNull final Context context, @Nullable final OrderedRealmCollection<Meal> data) {
        super(context, data);
    }

    @Override
    public View getView(final int i, @Nullable View view, final ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_mensa_detail_item, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.mensa_title);
            viewHolder.price = (TextView) view.findViewById(R.id.mensa_price);
            viewHolder.imageView = (NetworkImageView) view.findViewById(R.id.mensa_image);
            view.setTag(viewHolder);
        } else viewHolder = (ViewHolder) view.getTag();

        final Meal meal = getItem(i);
        if (meal == null)
            return view;

        // Title anzeigen
        if (meal.getTitle() != null)
            viewHolder.title.setText(meal.getTitle());

        // Preis anzeigen
        final String price = meal.getPrice();
        if (price != null) {
            if (price.matches("\\d+(?:\\.\\d+)?"))
                viewHolder.price.setText(context.getString(R.string.mensa_price, price));
            else viewHolder.price.setText(meal.getPrice());
        }

        // Vorschaubild laden
        if (!meal.getImageUrl().isEmpty()) {
            final ImageLoader imageLoader = VolleyDownloader.getInstance(context).getImageLoader();
            viewHolder.imageView.setImageUrl(meal.getImageUrl(), imageLoader);
            viewHolder.imageView.setVisibility(View.VISIBLE);
        } else viewHolder.imageView.setVisibility(View.GONE);

        return view;
    }

    private static class ViewHolder {
        TextView title;
        TextView price;
        NetworkImageView imageView;
    }
}
