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

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.htwdd.htwdresden.adapter.MensaOverviewMealAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.Realm;
import io.realm.RealmResults;

import static de.htwdd.htwdresden.MealDetailListFragment.ARG_CANTEEN_ID;
import static de.htwdd.htwdresden.classes.Const.database.Canteen.MENSA_DATE;
import static de.htwdd.htwdresden.classes.Const.database.Canteen.MENSA_ID;


/**
 * Fragment welches die einzelnen Gerichte anzeigt
 *
 * @author Kay Förster
 */
public class MensaDetailWeekFragment extends Fragment implements IRefreshing {
    private SwipeRefreshLayout swipeRefreshLayout;
    private int modus = 1;
    private Realm realm;

    private List<String> listDataHeader;
    private HashMap<String, RealmResults<Meal>> listDataChild;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();

        // Inflate the layout for this fragment
        final View mLayout = inflater.inflate(R.layout.listview_swipe_refresh_expandable, container, false);

        // Überprüfe Bundle & setze Modus
        final Bundle bundle = getArguments();
        if (bundle != null) {
            modus = bundle.getInt(Const.BundleParams.MENSA_DETAIL_MODE, 1);
        }

        // Setze Swipe Refresh Layout
        swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        ((TextView) mLayout.findViewById(R.id.message_info)).setText(R.string.mensa_no_offer_week);
        swipeRefreshLayout.setEnabled(false);

        // Setze Kalender auf Montag der ausgewählten Woche
        final Calendar beginOfWeek = GregorianCalendar.getInstance(Locale.GERMANY);
        beginOfWeek.set(Calendar.DAY_OF_WEEK, beginOfWeek.getFirstDayOfWeek());
        if (modus == 2) {
            beginOfWeek.roll(Calendar.WEEK_OF_YEAR, 1);
        }

        // get the listview
        ExpandableListView expListView = (ExpandableListView) mLayout.findViewById(R.id.expListView);

        assert bundle != null;
        int mensaId = bundle.getInt(ARG_CANTEEN_ID);

        // preparing list data
        prepareListData(beginOfWeek, mensaId);

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
            for ( int i = 0; i < count; i++ )
                expListView.expandGroup(i);

            expListView.setDividerHeight(8);

            ((TextView) mLayout.findViewById(R.id.message_info)).setVisibility(View.GONE);
        }

        return mLayout;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData(Calendar beginOfWeek, int mensaId) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, RealmResults<Meal>>();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        final String[] nameOfDays = DateFormatSymbols.getInstance().getWeekdays();

        for ( int i = 0; i < 5; i++) {
            // Bestimme Tag
            final Calendar calendar = (Calendar) beginOfWeek.clone();
            calendar.roll(Calendar.DAY_OF_WEEK, i);

            // Adding child data
            listDataHeader.add(nameOfDays[i + 2] + ", " + dateFormat.format(calendar.getTime()));
        }

        final Calendar calendar = (Calendar) beginOfWeek.clone();
        calendar.roll(Calendar.DAY_OF_WEEK, 0);

        // Adding child data
        RealmResults<Meal> montag = realm.where(Meal.class)
                .equalTo(MENSA_ID, mensaId)
                .equalTo(MENSA_DATE, MensaHelper.getDate(calendar))
                .findAll();

        calendar.roll(Calendar.DAY_OF_WEEK, 1);

        RealmResults<Meal> dienstag = realm.where(Meal.class)
                .equalTo(MENSA_ID, mensaId)
                .equalTo(MENSA_DATE, MensaHelper.getDate(calendar))
                .findAll();

        calendar.roll(Calendar.DAY_OF_WEEK, 1);

        RealmResults<Meal> mittwoch = realm.where(Meal.class)
                .equalTo(MENSA_ID, mensaId)
                .equalTo(MENSA_DATE, MensaHelper.getDate(calendar))
                .findAll();

        calendar.roll(Calendar.DAY_OF_WEEK, 1);

        Calendar cal = calendar;

        RealmResults<Meal> donnerstag = realm.where(Meal.class)
                .equalTo(MENSA_ID, mensaId)
                .equalTo(MENSA_DATE, MensaHelper.getDate(calendar))
                .findAll();

        calendar.roll(Calendar.DAY_OF_WEEK, 1);

        RealmResults<Meal> freitag = realm.where(Meal.class)
                .equalTo(MENSA_ID, mensaId)
                .equalTo(MENSA_DATE, MensaHelper.getDate(calendar))
                .findAll();

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), montag);
        listDataChild.put(listDataHeader.get(1), dienstag);
        listDataChild.put(listDataHeader.get(2), mittwoch);
        listDataChild.put(listDataHeader.get(3), donnerstag);
        listDataChild.put(listDataHeader.get(4), freitag);
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