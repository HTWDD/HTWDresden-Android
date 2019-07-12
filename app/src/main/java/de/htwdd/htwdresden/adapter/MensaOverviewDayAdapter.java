package de.htwdd.htwdresden.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
            viewHolder.imageGarlic = view.findViewById(R.id.meal_garlic);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Meal meal = getItem(i);
        if (meal == null) {
            return view;
        }

        viewHolder.title.setText(meal.getName());

        // Preis anzeigen
        final float priceStudent = (float) meal.getPrices().getStudents();
        final float priceEmployee = (float) meal.getPrices().getEmployees();
        viewHolder.price.setText(context.getString(R.string.mensa_price, priceStudent, priceEmployee));

        // Eigenschaften als Icon anzeigen
        viewHolder.imagePork.setVisibility(meal.getNotes().contains("Schweinefleisch") ? View.VISIBLE : View.GONE);
        viewHolder.imageBeef.setVisibility(meal.getNotes().contains("Rindfleisch") ? View.VISIBLE : View.GONE);
        viewHolder.imageVegetarian.setVisibility(meal.getNotes().contains("vegetarisch") ? View.VISIBLE : View.GONE);
        viewHolder.imageVegan.setVisibility(meal.getNotes().contains("vegan") ? View.VISIBLE : View.GONE);
        viewHolder.imageGarlic.setVisibility(meal.getNotes().contains("Knoblauch") ? View.VISIBLE : View.GONE);

        return view;
    }

    private static class ViewHolder {
        TextView title;
        TextView price;
        ImageView imagePork;
        ImageView imageBeef;
        ImageView imageVegetarian;
        ImageView imageVegan;
        ImageView imageGarlic;
        ImageView imageView;
    }
}
