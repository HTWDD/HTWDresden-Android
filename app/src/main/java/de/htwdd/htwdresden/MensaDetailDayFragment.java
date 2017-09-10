package de.htwdd.htwdresden;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.GregorianCalendar;

import de.htwdd.htwdresden.adapter.MensaOverviewDayAdapter;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.types.Meal;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Fragment welches die einzelnen Gerichte eines Tages anzeigt
 *
 * @author Kay Förster
 */
public class MensaDetailDayFragment extends Fragment {
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
        final SwipeRefreshLayout swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        ((TextView) mLayout.findViewById(R.id.message_info)).setText(R.string.mensa_no_offer);

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
                mensaHelper.loadAndSaveMeals(0);
            }
        });

        // Setze Adapter
        final RealmResults<Meal> realmResults = realm.where(Meal.class).equalTo("date", MensaHelper.getDate(GregorianCalendar.getInstance())).findAll();
        // Bei Änderungen an der Datenbasis Hinweismeldung überprüfen
        realmResults.addChangeListener(new RealmChangeListener<RealmResults<Meal>>() {
            @Override
            public void onChange(@NonNull final RealmResults<Meal> element) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        final MensaOverviewDayAdapter mensaArrayAdapter = new MensaOverviewDayAdapter(realmResults);
        listView.setAdapter(mensaArrayAdapter);
        listView.setEmptyView(mLayout.findViewById(R.id.message_info));
        // Setze Link für Details
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Meal meal = mensaArrayAdapter.getItem(i);
                if (meal != null && meal.getId() != 0) {
                    final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.studentenwerk-dresden.de/mensen/speiseplan/details-" + meal.getId() + ".html?pni=1"));
                    getActivity().startActivity(browserIntent);
                }
            }
        });

        return mLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}