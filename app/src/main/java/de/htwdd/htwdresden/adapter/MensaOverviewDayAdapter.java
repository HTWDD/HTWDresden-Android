package de.htwdd.htwdresden.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Adapter für die Mensa Tagesübersicht
 *
 * @author Kay Förster
 */
public class MensaOverviewDayAdapter extends RealmBaseAdapter<Meal> {

    public MensaOverviewDayAdapter(@Nullable final OrderedRealmCollection<Meal> data) {
        super(data);
    }

    @Override
    public View getView(final int i, @Nullable View view, final ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_mensa_detail_item_day, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.title = view.findViewById(R.id.mensa_title);
            viewHolder.price = view.findViewById(R.id.mensa_price);
            viewHolder.imageView = view.findViewById(R.id.mensa_image);
            viewHolder.imagePork = view.findViewById(R.id.mensa_pork);
            viewHolder.imageBeef = view.findViewById(R.id.mensa_beef);
            viewHolder.imageVegetarian = view.findViewById(R.id.meal_vegetarian);
            viewHolder.imageVegan = view.findViewById(R.id.meal_vegan);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Meal meal = getItem(i);
        if (meal == null) {
            return view;
        }

        viewHolder.title.setText(meal.getTitle());

        // Preis anzeigen
        if (!meal.isSoldOut()) {
            final float priceStudent = meal.getStudentPrice() != null ? meal.getStudentPrice() : 0f;
            final float priceEmployee = meal.getEmployeePrice() != null ? meal.getEmployeePrice() : 0f;
            viewHolder.price.setText(context.getString(R.string.mensa_price, priceStudent, priceEmployee));
        } else {
            viewHolder.price.setText(R.string.mensa_sold_out);
        }

        // Vorschaubild laden
        Picasso.get().load(meal.getImage()).placeholder(R.drawable.ic_meal_placeholder).into(viewHolder.imageView);

        // Eigenschaften als Icon anzeigen
        viewHolder.imagePork.setVisibility(meal.getInformation().contains("pork") ? View.VISIBLE : View.GONE);
        viewHolder.imageBeef.setVisibility(meal.getInformation().contains("beef") ? View.VISIBLE : View.GONE);
        viewHolder.imageVegetarian.setVisibility(meal.getInformation().contains("vegetarian") ? View.VISIBLE : View.GONE);
        viewHolder.imageVegan.setVisibility(meal.getInformation().contains("vegan") ? View.VISIBLE : View.GONE);

        return view;
    }

    private static class ViewHolder {
        TextView title;
        TextView price;
        ImageView imagePork;
        ImageView imageBeef;
        ImageView imageVegetarian;
        ImageView imageVegan;
        ImageView imageView;
    }
}
