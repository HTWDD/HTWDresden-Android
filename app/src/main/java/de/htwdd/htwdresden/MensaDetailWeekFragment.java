package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.htwdd.htwdresden.adapter.MensaOverviewWeekAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.classes.VolleyDownloader;
import de.htwdd.htwdresden.types.Meal;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Fragment welches die einzelnen Gerichte anzeigt
 *
 * @author Kay Förster
 */
public class MensaDetailWeekFragment extends Fragment {
    private int modus;

    public MensaDetailWeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mLayout = inflater.inflate(R.layout.listview_swipe_refresh, container, false);

        // Überprüfe Bundle & setze Modus
        final Bundle bundle = getArguments();
        if (bundle == null || bundle.getInt(Const.BundleParams.MENSA_DETAIL_MODE, -1) == -1)
            modus = 1;
        else
            modus = bundle.getInt(Const.BundleParams.MENSA_DETAIL_MODE);

        // Suche Views
        final ListView listView = (ListView) mLayout.findViewById(R.id.listView);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);

        // Setze Swipe Refresh Layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Context context = getActivity();
                // Überprüfe Internetverbindung
                if (!VolleyDownloader.CheckInternet(context)) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(context, R.string.info_no_internet, Toast.LENGTH_SHORT).show();
                    return;
                }
                final MensaHelper mensaHelper = new MensaHelper(context, (short) 9);
                mensaHelper.loadAndSaveMeals(modus);
            }
        });

        // Setze Kalender auf Montag der ausgewählten Woche
        final Calendar beginOfWeek = GregorianCalendar.getInstance();
        beginOfWeek.set(Calendar.DAY_OF_WEEK, beginOfWeek.getFirstDayOfWeek());
        if (modus == 2) {
            beginOfWeek.add(Calendar.WEEK_OF_YEAR, 1);
        }

        // Hole Daten aus DB
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Meal> realmResults = realm.where(Meal.class).findAll();
        // Bei Änderungen an der Datenbasis Aktualisierung ausschalten
        realmResults.addChangeListener(new RealmChangeListener<RealmResults<Meal>>() {
            @Override
            public void onChange(final RealmResults<Meal> element) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        listView.setAdapter(new MensaOverviewWeekAdapter(getActivity(), beginOfWeek, realmResults));

        return mLayout;
    }
}