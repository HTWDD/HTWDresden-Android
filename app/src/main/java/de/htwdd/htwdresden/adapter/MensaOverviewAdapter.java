package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.canteen.Canteen;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

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
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Canteen canteen = getItem(i);
        if (canteen == null) {
            return view;
        }

        String adressShortened = canteen.getAddress().replaceAll(", Deutschland", "");
        adressShortened = adressShortened.substring(0, adressShortened.indexOf(','));

        viewHolder.name.setText(trimName(canteen.getName()));
        viewHolder.adresse.setText(adressShortened);
        viewHolder.city.setText(canteen.getCity());

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
    }
}
