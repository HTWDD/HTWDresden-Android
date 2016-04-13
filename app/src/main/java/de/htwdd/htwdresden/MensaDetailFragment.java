package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.nio.charset.Charset;
import java.util.ArrayList;

import de.htwdd.htwdresden.adapter.MensaArrayAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.classes.VolleyDownloader;
import de.htwdd.htwdresden.types.Meal;


/**
 * Fragment welches die einzelnen Gerichte anzeigt
 */
public class MensaDetailFragment extends Fragment {
    short mensaID = 9;
    private View mLayout;
    private int modus;
    // Liste zum Anzeigen
    private ArrayList<Meal> meals;
    // Adapter für die Liste
    MensaArrayAdapter mensaArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        meals = new ArrayList<>();
        mensaArrayAdapter = new MensaArrayAdapter(getActivity(), meals);

        // Überprüfe Bundle & setze Modus
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getInt(Const.BundleParams.MENSA_DETAIL_MODE, -1) == -1)
            modus = 0;
        else
            modus = bundle.getInt(Const.BundleParams.MENSA_DETAIL_MODE);
    }

    public MensaDetailFragment() {
        // Required empty public constructor
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
                loadData();
            }
        });

        // Setze Adapter
        listView.setAdapter(mensaArrayAdapter);
        // Setze Link für Details
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Meal meal = meals.get(i);
                if (meal.getId() != 0) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.studentenwerk-dresden.de/mensen/speiseplan/details-" + meal.getId() + ".html?pni=1"));
                    getActivity().startActivity(browserIntent);
                }
            }
        });

        // Lade Pläne
        loadData();

        return mLayout;
    }

    /**
     * Lädt die entsprechenden Plane je nach Modus
     */
    private void loadData() {
        final TextView textView = (TextView) mLayout.findViewById(R.id.message_info);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Liste ausblenden
                meals.clear();
                // Fehlermeldung anzeigen
                textView.setText(R.string.info_error);
                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textView.setText(null);
                swipeRefreshLayout.setRefreshing(false);
                meals.clear();

                final MensaHelper mensaHelper = new MensaHelper(getActivity(), mensaID);
                switch (modus) {
                    case 1:
                    case 2:
                        // Ändere Encoding
                        response = new String(response.getBytes(Charset.forName("iso-8859-1")), Charset.forName("UTF-8"));
                        // Parse Ergebniss
                        meals.addAll(mensaHelper.parseCompleteWeek(response));
                        break;
                    default:
                        // Parse Ergebniss
                        meals.addAll(mensaHelper.parseCurrentDay(response));
                        break;
                }

                if (meals.size() == 0)
                    textView.setText(R.string.mensa_no_offer);

                // Adapter über neue Daten informieren
                mensaArrayAdapter.notifyDataSetChanged();
            }
        };

        // Starte Refreshing
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        // Wähle URL aus
        final String url = MensaHelper.getMensaUrl(modus);

        // Überprüfe Internetverbindung
        if (!VolleyDownloader.CheckInternet(getActivity())) {
            // Liste ausblenden
            meals.clear();
            // Refresh ausschalten
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            // Meldung anzeigen
            textView.setText(R.string.info_no_internet);
            return;
        }

        // Download der Informationen
        final StringRequest stringRequest = new StringRequest(url, stringListener, errorListener);
        VolleyDownloader.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}