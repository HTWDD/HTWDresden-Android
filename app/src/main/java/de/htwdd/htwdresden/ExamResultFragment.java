package de.htwdd.htwdresden;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import de.htwdd.htwdresden.adapter.ExamResultListAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.classes.ExamsHelper;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.ExamResultDAO;
import de.htwdd.htwdresden.events.UpdateExamResultsEvent;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.service.ExamSyncService;
import de.htwdd.htwdresden.types.ExamResult;
import io.realm.Realm;


/**
 * Fragment zur Anzeige der Prüfungsergebnisse
 *
 * @author Kay Förster
 */
public class ExamResultFragment extends Fragment {
    private ResponseReceiver responseReceiver;
    private long countExamResults = 0;
    private HashMap<Integer, ArrayList<ExamResult>> listExamResults = new HashMap<>();
    private ExamResultListAdapter adapter;
    private View mLayout;

    public ExamResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Context context = getActivity();
        mLayout = inflater.inflate(R.layout.fragment_exams_result, container, false);
        adapter = new ExamResultListAdapter(getActivity(), listExamResults);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Context context = getActivity();
                // Überprüfe ob Internetverbindung besteht
                if (!VolleyDownloader.CheckInternet(context)) {
                    // Refresh ausschalten
                    swipeRefreshLayout.setRefreshing(false);
                    showMessage(context.getString(R.string.info_no_internet));
                    return;
                }
                // Überprüfe ob Einstellungen richtig gesetzt sind
                if (!ExamsHelper.checkPreferences(context)) {
                    // Refresh ausschalten
                    swipeRefreshLayout.setRefreshing(false);
                    // Snackbar mit Information anzeigen
                    Snackbar.make(mLayout, R.string.info_no_settings, Snackbar.LENGTH_LONG)
                            .setAction(R.string.navi_settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Navigation ändern
                                    ((INavigation) getActivity()).setNavigationItem(R.id.navigation_settings);
                                    // Fragment für die Einstellungen öffnen
                                    final FragmentManager fragmentManager = getActivity().getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.activity_main_FrameLayout, new SettingsFragment()).addToBackStack("back").commit();
                                }
                            })
                            .show();
                    return;
                }

                // Service zum Updaten starten
                context.startService(new Intent(context, ExamSyncService.class));
            }
        });

        // Daten aus Datenbank laden
        final Realm realm = Realm.getDefaultInstance();
        countExamResults = realm.where(ExamResult.class).count();

        final ExpandableListView expandableListView = (ExpandableListView) mLayout.findViewById(R.id.expandableListView);
        expandableListView.setAdapter(adapter);

        showData();

        // IntentReceiver erstellen
        final IntentFilter intentFilter = new IntentFilter(Const.IntentParams.BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.addCategory(ExamSyncService.INTENT_SYNC_EXAMS);
        responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(responseReceiver, intentFilter);

        return mLayout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
        EventBus.getInstance().unregister(this);
    }

    /**
     * Zeigt Infos entweder direkt im View oder per Toast an
     *
     * @param message anzuzeigende Information
     */
    private void showMessage(@NonNull final String message) {
        // Wenn ListView keine Daten enthält als Text anzeigen, ansonsten per Toast
        if (adapter.getGroupCount() == 0) {
            final TextView textViewMessage = (TextView) mLayout.findViewById(R.id.info_message);
            textViewMessage.setText(message);
        } else {
            final Context context = getActivity();
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Empfänger für Updates der Noten vom Service
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            if (intent.getIntExtra(Const.IntentParams.BROADCAST_CODE, -1) == 0) {
                final Realm realm = Realm.getDefaultInstance();
                final long newCountExamResults = realm.where(ExamResult.class).count();
                if (newCountExamResults > countExamResults) {
                    Toast.makeText(getActivity(), R.string.exams_result_update_newGrades, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.exams_result_update_noNewGrades, Toast.LENGTH_SHORT).show();
                }
                countExamResults = newCountExamResults;
            } else if (intent.hasExtra(Const.IntentParams.BROADCAST_MESSAGE)) {
                showMessage(intent.getStringExtra(Const.IntentParams.BROADCAST_MESSAGE));
            } else showMessage(context.getString(R.string.info_error_save));
        }
    }


    /**
     * Behandelt die Benachrichtigung vom Eventbus das neue Prüfungsergebnisse zur Verfügung stehen
     *
     * @param updateExamResultsEvent Typ der Benachrichtigung
     */
    @Subscribe
    public void updateExamResults(UpdateExamResultsEvent updateExamResultsEvent){
        showData();
    }

    /**
     * Zeigt bzw. Aktualisiert die Daten im View
     */
    private void showData() {
        final TextView message = (TextView) mLayout.findViewById(R.id.info_message);

        // Lade Daten aus Datenbank
        final ExamResultDAO dao = new ExamResultDAO(new DatabaseManager(getActivity()));
        final ArrayList<ExamResult> examResults = dao.getAll();

        // Daten in HashMap umwandeln
        listExamResults.clear();
        for (ExamResult examResult : examResults) {
            if (!listExamResults.containsKey(examResult.semester))
                listExamResults.put(examResult.semester, new ArrayList<ExamResult>());
            listExamResults.get(examResult.semester).add(examResult);
        }

        adapter.notifyDataSetChanged();

        // Meldung anzeigen
        if (examResults.size() == 0) {
            message.setText(R.string.exams_result_no_results);
            mLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_background));
        } else {
            message.setText(null);
            mLayout.setBackgroundColor(Color.WHITE);
        }
    }
}
