package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.text.NumberFormat;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Adapter für die Mensa Tagesübersicht
 *
 * @author Kay Förster
 */
public class MensaOverviewDayAdapter extends RealmBaseAdapter<Meal> {
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public MensaOverviewDayAdapter(@Nullable final OrderedRealmCollection<Meal> data) {
        super(data);
    }

    @Override
    public View getView(final int i, @Nullable View view, final ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_mensa_detail_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.title = view.findViewById(R.id.mensa_title);
            viewHolder.price = view.findViewById(R.id.mensa_price);
            viewHolder.imageView = view.findViewById(R.id.mensa_image);
            view.setTag(viewHolder);
        } else viewHolder = (ViewHolder) view.getTag();

        final Meal meal = getItem(i);
        if (meal == null) {
            return view;
        }

        viewHolder.title.setText(meal.getTitle());

        // Preis setzen
        if (meal.getStudentPrice() != null){
            viewHolder.price.setText(context.getString(R.string.mensa_price, numberFormat.format(meal.getStudentPrice())));
        }

        // Vorschaubild laden
        if (meal.getImage() != null) {
            final ImageLoader imageLoader = VolleyDownloader.getInstance(context).getImageLoader();
            viewHolder.imageView.setImageUrl(meal.getImage(), imageLoader);
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
