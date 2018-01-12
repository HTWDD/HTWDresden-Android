package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Adapter für die Mensa Wochenübersicht
 *
 * @author Kay Förster
 */
public class MensaOverviewWeekAdapter extends RealmBaseAdapter<Meal> {
    private static final String[] nameOfDays = DateFormatSymbols.getInstance().getWeekdays();
    private final Calendar beginOfWeek;

    public MensaOverviewWeekAdapter(@NonNull final Calendar beginOfWeek, @Nullable final OrderedRealmCollection<Meal> data) {
        super(data);
        this.beginOfWeek = beginOfWeek;
    }

    @Override
    public int getCount() {
        return 5;
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
            viewHolder.imageView.setVisibility(View.GONE);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Bestimme Tag
        final Calendar calendar = (Calendar) beginOfWeek.clone();
        calendar.roll(Calendar.DAY_OF_WEEK, i);

        // Gerichte für den jeweiligen Tag laden
        if (adapterData == null) {
            return view;
        }

        final RealmResults<Meal> realmResults = adapterData.where().equalTo(Const.database.Canteen.MENSA_DATE, MensaHelper.getDate(calendar)).findAll();
        viewHolder.title.setText(nameOfDays[i + 2]);
        viewHolder.price.setText(MensaHelper.concatTitels(context, realmResults));

        return view;
    }


    private static class ViewHolder {
        TextView title;
        TextView price;
        NetworkImageView imageView;
    }
}
