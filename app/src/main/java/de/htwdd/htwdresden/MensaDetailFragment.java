package de.htwdd.htwdresden;


import android.app.Fragment;
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

import com.squareup.otto.Subscribe;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.htwdd.htwdresden.adapter.MensaArrayAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.database.MensaDAO;
import de.htwdd.htwdresden.events.UpdateMensaEvent;
import de.htwdd.htwdresden.types.Meal;


/**
 * Fragment welches die einzelnen Gerichte anzeigt
 *
 * @author Kay Förster
 */
public class MensaDetailFragment extends Fragment {
    private View mLayout;
    private int modus;
    private final ArrayList<Meal> meals = new ArrayList<>();
    private static final String[] nameOfDays = DateFormatSymbols.getInstance().getWeekdays();
    // Adapter für die Liste
    private MensaArrayAdapter mensaArrayAdapter;

    public MensaDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Überprüfe Bundle & setze Modus
        final Bundle bundle = getArguments();
        if (bundle == null || bundle.getInt(Const.BundleParams.MENSA_DETAIL_MODE, -1) == -1)
            modus = 0;
        else
            modus = bundle.getInt(Const.BundleParams.MENSA_DETAIL_MODE);

        EventBus.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.listview_swipe_refresh, container, false);

        // Suche Views
        final ListView listView = (ListView) mLayout.findViewById(R.id.listView);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);

        // Setze Swipe Refresh Layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final MensaHelper mensaHelper = new MensaHelper(getActivity(), (short) 9);
                mensaHelper.loadAndSaveMeals(modus);
            }
        });

        // Setze Adapter
        mensaArrayAdapter = new MensaArrayAdapter(getActivity(), meals);
        listView.setAdapter(mensaArrayAdapter);
        // Setze Link für Details
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Meal meal = meals.get(i);
                if (meal.getId() != 0) {
                    final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.studentenwerk-dresden.de/mensen/speiseplan/details-" + meal.getId() + ".html?pni=1"));
                    getActivity().startActivity(browserIntent);
                }
            }
        });

        // Lade Daten
        updateMensa(null);

        return mLayout;
    }

    @Subscribe
    public void updateMensa(@Nullable final UpdateMensaEvent updateMensaEvent) {
        // Wenn Event vorhanden und der Modus nicht stimmen dieses Event ignorieren
        if (updateMensaEvent != null && updateMensaEvent.getForModus() != modus)
            return;

        // Alte Mahlzeiten entfernen
        meals.clear();

        // Abhängig vom Modus die Mahlzeiten laden und aufbereiten
        final Calendar calendar = GregorianCalendar.getInstance();
        if (modus == 0) {
            meals.addAll(MensaDAO.getMealsByDate(calendar));
        } else {
            // Für nächste Woche eine Woche im Kalender hinzufügen
            if (modus == 2) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            // Kalender auf ersten Tag in der Woche setzen
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            for (int i = 0; i < 5; i++) {
                final ArrayList<Meal> mealsDay = MensaDAO.getMealsByDate(calendar);
                final StringBuilder stringBuilder = new StringBuilder();
                final int countMeals = mealsDay.size();
                for (int j = 0; j < countMeals - 1; j++) {
                    stringBuilder.append(mealsDay.get(j).getTitle());
                    stringBuilder.append("\n\n");
                }

                // Aktuell kein Angebot vorhanden
                if (countMeals != 0) {
                    stringBuilder.append(mealsDay.get(countMeals - 1).getTitle());
                } else stringBuilder.append(getString(R.string.mensa_no_offer));

                // Ansicht erstellen
                final Meal meal = new Meal();
                meal.setTitle(nameOfDays[i + 2]);
                meal.setDate(calendar);
                meal.setPrice(stringBuilder.toString());
                meals.add(meal);

                // Kalender auf nächsten Tag setzen
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        // Wenn keine Essen gespeichert, Meldung ausgeben
        if (meals.size() == 0) {
            final TextView messageView = (TextView) mLayout.findViewById(R.id.message_info);
            messageView.setText(R.string.mensa_no_offer);
        }

        // Adapter über neue Daten informieren
        mensaArrayAdapter.notifyDataSetChanged();

        // Refreshing ausschalten
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
    }
}