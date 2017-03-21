package de.htwdd.htwdresden;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import de.htwdd.htwdresden.classes.Const;
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
    private View mLayout;
    private int modus;
    private MensaOverviewDayAdapter mensaArrayAdapter;

    public MensaDetailDayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Überprüfe Bundle & setze Modus
        final Bundle bundle = getArguments();
        if (bundle == null || bundle.getInt(Const.BundleParams.MENSA_DETAIL_MODE, -1) == -1)
            modus = 0;
        else
            modus = bundle.getInt(Const.BundleParams.MENSA_DETAIL_MODE);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.listview_swipe_refresh, container, false);

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

        // Hole Daten aus DB
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Meal> realmResults = realm.where(Meal.class).equalTo("date", MensaHelper.getDate(GregorianCalendar.getInstance())).findAll();
        // Bei Änderungen an der Datenbasis Hinweismeldung überprüfen
        realmResults.addChangeListener(new RealmChangeListener<RealmResults<Meal>>() {
            @Override
            public void onChange(final RealmResults<Meal> element) {
                swipeRefreshLayout.setRefreshing(false);
                setDataInfoMessage();
            }
        });

        // Setze Adapter
        mensaArrayAdapter = new MensaOverviewDayAdapter(realmResults);
        listView.setAdapter(mensaArrayAdapter);

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

        // Hinweismeldung anzeigen
        setDataInfoMessage();

        return mLayout;
    }

    /**
     * Setzt eine Hinweismeldung falls aktuell keine Daten vorliegen
     */
    private void setDataInfoMessage() {
        // Wenn keine Essen gespeichert, Meldung ausgeben
        final TextView messageView = (TextView) mLayout.findViewById(R.id.message_info);
        if (mensaArrayAdapter.getCount() == 0) {
            messageView.setText(R.string.mensa_no_offer);
        } else messageView.setText(null);
    }
}