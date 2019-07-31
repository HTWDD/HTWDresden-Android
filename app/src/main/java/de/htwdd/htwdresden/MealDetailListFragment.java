package de.htwdd.htwdresden;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.htwdd.htwdresden.adapter.MensaOverviewMealAdapter;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static de.htwdd.htwdresden.classes.Const.database.Canteen.MENSA_CATEGORY;
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

    private List<String> listDataHeader;
    private HashMap<String, RealmResults<Meal>> listDataChild;

    public static final String ARG_CANTEEN_ID = "canteen_id";

    public MealDetailListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mLayout = inflater.inflate(R.layout.listview_swipe_refresh_expandable, container, false);

        realm = Realm.getDefaultInstance();
        // Suche Views
        swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        ((TextView) mLayout.findViewById(R.id.message_info)).setText(R.string.mensa_no_offer_day);

        // Setze Swipe Refresh Layout
        swipeRefreshLayout.setEnabled(false);

        // get the listview
        ExpandableListView expListView = (ExpandableListView) mLayout.findViewById(R.id.expListView);

        // preparing list data
        prepareListData();

        int j = 0;

        for (RealmResults<Meal> mealList : listDataChild.values()) {
            if ( mealList.size() == 0) {
                j++;
            }
        }

        if(j != listDataHeader.size()) {
            MensaOverviewMealAdapter listAdapter = new MensaOverviewMealAdapter(this.getContext(), listDataHeader, listDataChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);

            int count = listAdapter.getGroupCount();
            for (int i = 0; i < count; i++)
                expListView.expandGroup(i);

            expListView.setDividerHeight(8);
            ((TextView) mLayout.findViewById(R.id.message_info)).setVisibility(View.GONE);
        }
        return mLayout;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, RealmResults<Meal>>();

        final RealmResults<Meal> realmResults = realm.where(Meal.class)
                .equalTo(MENSA_ID, (short) getArguments().getInt(ARG_CANTEEN_ID))
                .equalTo(MENSA_DATE, MensaHelper.getDate(Calendar.getInstance()))
                .sort(MENSA_CATEGORY, Sort.ASCENDING)
                .findAll();

        for (Meal meal : realmResults) {
            listDataHeader.add(meal.getCategory());
        }

        HashSet<String> hashSet = new HashSet<String>(listDataHeader);
        listDataHeader.clear();
        listDataHeader.addAll(hashSet);

        java.util.Collections.sort(listDataHeader);

        int i = 0;

        for (String cat : listDataHeader) {
            RealmResults<Meal> meals = realmResults.where().equalTo(MENSA_CATEGORY, cat).findAll();

            listDataChild.put(listDataHeader.get(i), meals);

            i++;
        }
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