package de.htwdd.htwdresden;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
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

import de.htwdd.htwdresden.adapter.ExamResultAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.ExamsHelper;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.interfaces.INavigation;
import de.htwdd.htwdresden.service.ExamSyncService;
import de.htwdd.htwdresden.types.ExamResult;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Fragment zur Anzeige der Prüfungsergebnisse
 *
 * @author Kay Förster
 */
public class ExamResultFragment extends Fragment {
    private RealmChangeListener<RealmResults<ExamResult>> realmListenerExams;
    private RealmResults<ExamResult> examResults;
    private ResponseReceiver responseReceiver;
    private long countExamResults = 0;
    private ExamResultAdapter adapter;
    private View mLayout;

    public ExamResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Context context = getActivity();
        mLayout = inflater.inflate(R.layout.fragment_exams_result, container, false);
        adapter = new ExamResultAdapter(context);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Context context = getActivity();
                // Überprüfe ob Internetverbindung besteht
                if (!VolleyDownloader.CheckInternet(context)) {
                    // Refresh ausschalten
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(context, R.string.info_no_internet, Toast.LENGTH_LONG).show();
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

        // Auf Änderungen an der Datenbank hören
        realmListenerExams = new RealmChangeListener<RealmResults<ExamResult>>() {
            @Override
            public void onChange(final RealmResults<ExamResult> element) {
                adapter.notifyDataSetChanged();
                showMessageNoExamResults(element.size() > 0);
            }
        };
        examResults = realm.where(ExamResult.class).findAll();
        examResults.addChangeListener(realmListenerExams);
        showMessageNoExamResults(countExamResults > 0);

        final ExpandableListView expandableListView = (ExpandableListView) mLayout.findViewById(R.id.expandableListView);
        expandableListView.setAdapter(adapter);

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
        examResults.removeChangeListener(realmListenerExams);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
    }

    /**
     * Hinweismeldung anzeigen wenn keine Noten vorhanden sind
     *
     * @param examsAvailable Sind Noten vorhanden?
     */
    private void showMessageNoExamResults(final boolean examsAvailable) {
        final TextView infoMessage = (TextView) mLayout.findViewById(R.id.info_message);
        if (examsAvailable) {
            infoMessage.setText(null);
            mLayout.setBackgroundColor(Color.WHITE);
        } else {
            infoMessage.setText(R.string.exams_result_no_results);
            mLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_background));
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
                    Toast.makeText(context, R.string.exams_result_update_newGrades, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.exams_result_update_noNewGrades, Toast.LENGTH_SHORT).show();
                }
                countExamResults = newCountExamResults;
            } else if (intent.hasExtra(Const.IntentParams.BROADCAST_MESSAGE)) {
                Toast.makeText(context, intent.getStringExtra(Const.IntentParams.BROADCAST_MESSAGE), Toast.LENGTH_LONG).show();
            } else Toast.makeText(context, R.string.info_error_save, Toast.LENGTH_LONG).show();
        }
    }
}
