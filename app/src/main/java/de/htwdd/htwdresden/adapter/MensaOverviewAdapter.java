package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Calendar;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.types.canteen.Canteen;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

import static de.htwdd.htwdresden.classes.Const.database.Canteen.MENSA_DATE;
import static de.htwdd.htwdresden.classes.Const.database.Canteen.MENSA_ID;

/**
 * Adapter für die Mensa Tagesübersicht
 *
 * @author Kay Förster
 */
public class MensaOverviewAdapter extends RealmBaseAdapter<Canteen> {

    public MensaOverviewAdapter(@Nullable final OrderedRealmCollection<Canteen> data) {
        super(data);
    }

    @Override
    public View getView(final int i, @Nullable View view, final ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_mensa_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.name = view.findViewById(R.id.mensa_name);
            viewHolder.adresse = view.findViewById(R.id.mensa_adresse);
            viewHolder.city = view.findViewById(R.id.mensa_city);
            viewHolder.mealNumber = view.findViewById(R.id.meal_number);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Canteen canteen = getItem(i);
        if (canteen == null) {
            return view;
        }

        String addressShortened = canteen.getAddress().replaceAll(", Deutschland", "");
        addressShortened = addressShortened.substring(0, addressShortened.indexOf(','));

        viewHolder.name.setText(trimName(canteen.getName()));
        viewHolder.adresse.setText(addressShortened);
        viewHolder.city.setText(canteen.getCity());

        Realm realm = Realm.getDefaultInstance();

        RealmResults<Meal> realmResults = realm.where(Meal.class).equalTo(MENSA_ID, canteen.getId()).equalTo(MENSA_DATE, MensaHelper.getDate(Calendar.getInstance())).findAll();

        realmResults.addChangeListener((results, changeSet) -> {
            // Query results are updated in real time with fine grained notifications.
            changeSet.getInsertions(); // => [0] is added.
        });

        int mealNum = realm.where(Meal.class).equalTo(MENSA_ID, canteen.getId()).equalTo(MENSA_DATE, MensaHelper.getDate(Calendar.getInstance())).findAll().size();

        if(mealNum > 0) {
            viewHolder.mealNumber.setTextColor(Color.rgb(0,0,0));
            viewHolder.mealNumber.setBackgroundResource(R.drawable.rounded_corners_background_light_gray);
            viewHolder.mealNumber.setText(String.valueOf(mealNum));
        }
        else {
            viewHolder.mealNumber.setTextColor(Color.rgb(255,255,255));
            viewHolder.mealNumber.setBackgroundResource(R.drawable.rounded_corners_background_red);
            viewHolder.mealNumber.setText(String.valueOf(mealNum));
        }

        return view;
    }

    private String trimName(String name) {
        String shortName = name.replaceAll("Dresden, ", "");
        shortName = shortName.replaceAll("Tharandt, ", "");

        return shortName;
    }

    private static class ViewHolder {
        TextView name;
        TextView adresse;
        TextView city;
        TextView mealNumber;
    }
}
