package de.htwdd.htwdresden;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Calendar;

import de.htwdd.htwdresden.adapter.MensaOverviewDayAdapter;
import de.htwdd.htwdresden.classes.ConnectionHelper;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.Realm;
import io.realm.RealmResults;

import static de.htwdd.htwdresden.classes.Const.database.Canteen.MENSA_DATE;
import static de.htwdd.htwdresden.classes.Const.database.Canteen.MENSA_ID;

/**
 * Fragment welches die einzelnen Mensen anzeigt
 *
 * @author Kay Förster
 */
public class MealDetailListFragment extends Fragment implements IRefreshing {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Realm realm;

    public static final String ARG_CANTEEN_ID = "canteen_id";

    public MealDetailListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mLayout = inflater.inflate(R.layout.listview_swipe_refresh, container, false);

        realm = Realm.getDefaultInstance();
        // Suche Views
        final ListView listView = mLayout.findViewById(R.id.listView);
        swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        ((TextView) mLayout.findViewById(R.id.message_info)).setText(R.string.mensa_no_offer);

        // Setze Swipe Refresh Layout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            final Context context = getContext();
            if (context == null) {
                return;
            }
            // Überprüfe Internetverbindung
            if (ConnectionHelper.checkNoInternetConnection(context)) {
                onCompletion();
                Toast.makeText(context, R.string.info_no_internet, Toast.LENGTH_SHORT).show();
                return;
            }
        });



        // Setze Adapter
        assert getArguments() != null;
        final RealmResults<Meal> realmResults = realm.where(Meal.class)
                .equalTo(MENSA_ID, Short.valueOf(getArguments().getString(ARG_CANTEEN_ID))).equalTo(MENSA_DATE, MensaHelper.getDate(Calendar.getInstance()))
                .findAll();
        final MensaOverviewDayAdapter mensaArrayAdapter = new MensaOverviewDayAdapter(realmResults);
        listView.setAdapter(mensaArrayAdapter);
        listView.setEmptyView(mLayout.findViewById(R.id.message_info));
        // Default Divider
        final TypedArray typedArray = mLayout.getContext().obtainStyledAttributes(new int[]{ android.R.attr.listDivider });
        listView.setDividerHeight(8);
        typedArray.recycle();

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