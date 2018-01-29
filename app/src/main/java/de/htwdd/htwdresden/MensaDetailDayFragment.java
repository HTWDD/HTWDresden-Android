package de.htwdd.htwdresden;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.GregorianCalendar;

import de.htwdd.htwdresden.adapter.MensaOverviewDayAdapter;
import de.htwdd.htwdresden.classes.ConnectionHelper;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Fragment welches die einzelnen Gerichte eines Tages anzeigt
 *
 * @author Kay Förster
 */
public class MensaDetailDayFragment extends Fragment implements IRefreshing {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Realm realm;

    public MensaDetailDayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mLayout = inflater.inflate(R.layout.listview_swipe_refresh, container, false);
        realm = Realm.getDefaultInstance();

        // Suche Views
        final ListView listView = mLayout.findViewById(R.id.listView);
        swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        ((TextView) mLayout.findViewById(R.id.message_info)).setText(R.string.mensa_no_offer);

        // Setze Swipe Refresh Layout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            final Context context = getActivity();
            // Überprüfe Internetverbindung
            if (!ConnectionHelper.checkInternetConnection(context)) {
                onCompletion();
                Toast.makeText(context, R.string.info_no_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            final MensaHelper mensaHelper = new MensaHelper(context, (short) 1);
            mensaHelper.updateMeals(this);
        });

        // Setze Adapter
        final RealmResults<Meal> realmResults = realm.where(Meal.class)
                .equalTo(Const.database.Canteen.MENSA_DATE, MensaHelper.getDate(GregorianCalendar.getInstance()))
                .sort(Const.database.Canteen.MENSA_IS_SOLDOUT, Sort.ASCENDING, Const.database.Canteen.MENSA_IMAGE, Sort.DESCENDING)
                .findAll();
        final MensaOverviewDayAdapter mensaArrayAdapter = new MensaOverviewDayAdapter(realmResults);
        listView.setAdapter(mensaArrayAdapter);
        listView.setEmptyView(mLayout.findViewById(R.id.message_info));
        // Default Divider
        final TypedArray typedArray = mLayout.getContext().obtainStyledAttributes(new int[]{ android.R.attr.listDivider });
        listView.setDivider(typedArray.getDrawable(0));
        typedArray.recycle();
        // Setze Link für Details
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            final Meal meal = mensaArrayAdapter.getItem(i);
            if (meal != null) {
                final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(meal.getDetailURL()));
                getActivity().startActivity(browserIntent);
            }
        });

        return mLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    @Override
    public void onCompletion() {
        if (!isDetached()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}