package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

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
            viewHolder.price_student = view.findViewById(R.id.mensa_price_student);
            viewHolder.price_employee = view.findViewById(R.id.mensa_price_employee);
            viewHolder.imagePork = view.findViewById(R.id.mensa_layout_pork);
            viewHolder.imageBeef = view.findViewById(R.id.mensa_layout_beef);
            viewHolder.imageVegetarian = view.findViewById(R.id.mensa_layout_vegetarian);
            viewHolder.imageVegan = view.findViewById(R.id.mensa_layout_vegan);
            viewHolder.imageGarlic = view.findViewById(R.id.mensa_layout_garlic);
            viewHolder.imageAlcohol = view.findViewById(R.id.mensa_layout_alcohol);
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

        viewHolder.price_student.setText(priceStudent == 0 ? context.getString(R.string.mensa_price_student_no_price) : context.getString(R.string.mensa_price_student, priceStudent));
        viewHolder.price_employee.setText(priceEmployee == 0 ? context.getString(R.string.mensa_price_employee_no_price) : context.getString(R.string.mensa_price_employee, priceEmployee));

        // Eigenschaften als Icon anzeigen
        viewHolder.imagePork.setVisibility(meal.getNotes().contains("enthält Schweinefleisch") ? View.VISIBLE : View.GONE);
        viewHolder.imageBeef.setVisibility(meal.getNotes().contains("enthält Rindfleisch") ? View.VISIBLE : View.GONE);
        viewHolder.imageVegetarian.setVisibility(meal.getNotes().contains("vegetarisch") ? View.VISIBLE : View.GONE);
        viewHolder.imageVegan.setVisibility(meal.getNotes().contains("vegan") ? View.VISIBLE : View.GONE);
        viewHolder.imageGarlic.setVisibility(meal.getNotes().contains("enthält Knoblauch") ? View.VISIBLE : View.GONE);
        viewHolder.imageAlcohol.setVisibility(meal.getNotes().contains("enthält Alkohol") ? View.VISIBLE : View.GONE);

        return view;
    }

    private static class ViewHolder {
        TextView title;
        TextView price_student;
        TextView price_employee;
        LinearLayout imagePork;
        LinearLayout imageBeef;
        LinearLayout imageVegetarian;
        LinearLayout imageVegan;
        LinearLayout imageGarlic;
        LinearLayout imageAlcohol;
    }
}
