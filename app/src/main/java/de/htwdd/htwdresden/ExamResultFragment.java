package de.htwdd.htwdresden;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import de.htwdd.htwdresden.account.AuthenticatorActivity;
import de.htwdd.htwdresden.adapter.ExamResultAdapter;
import de.htwdd.htwdresden.classes.ConnectionHelper;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.ExamsHelper;
import de.htwdd.htwdresden.service.ExamSyncService;
import de.htwdd.htwdresden.types.exams.ExamResult;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Fragment zur Anzeige der Prüfungsergebnisse
 *
 * @author Kay Förster
 */
public class ExamResultFragment extends Fragment {
    private Realm realm;
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
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_exams_result, container, false);
        realm = Realm.getDefaultInstance();
        adapter = new ExamResultAdapter(mLayout.getContext(), realm);

        final SwipeRefreshLayout swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            final Context context1 = mLayout.getContext();
            // Überprüfe ob Internetverbindung besteht
            if (ConnectionHelper.checkNoInternetConnection(context1)) {
                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(context1, R.string.info_no_internet, Toast.LENGTH_LONG).show();
                return;
            }
            // Überprüfe ob Einstellungen richtig gesetzt sind
            if (!ExamsHelper.checkPreferences(context1)) {
                // Refresh ausschalten
                swipeRefreshLayout.setRefreshing(false);
                // Snackbar mit Information anzeigen
                Snackbar.make(mLayout, R.string.info_no_settings, Snackbar.LENGTH_LONG)
                        .setAction(R.string.sign_in, view -> {
                            final Context context = requireContext();
                            context.startActivity(new Intent(context, AuthenticatorActivity.class));
                        })
                        .show();
                return;
            }

            // Service zum Updaten starten
            context1.startService(new Intent(context1, ExamSyncService.class));
        });

        // Daten aus Datenbank laden
        countExamResults = realm.where(ExamResult.class).count();

        // Auf Änderungen an der Datenbank hören
        realmListenerExams = element -> adapter.notifyDataSetChanged();
        examResults = realm.where(ExamResult.class).findAll();
        examResults.addChangeListener(realmListenerExams);

        final ExpandableListView expandableListView = mLayout.findViewById(R.id.expandableListView);
        expandableListView.setAdapter(adapter);
        expandableListView.setEmptyView(mLayout.findViewById(R.id.info_message));
        expandableListView.addHeaderView(inflater.inflate(R.layout.exams_header, expandableListView, false));

        // IntentReceiver erstellen
        final IntentFilter intentFilter = new IntentFilter(Const.IntentParams.BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.addCategory(ExamSyncService.INTENT_SYNC_EXAMS);
        responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(mLayout.getContext()).registerReceiver(responseReceiver, intentFilter);

        return mLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(mLayout.getContext()).unregisterReceiver(responseReceiver);
        examResults.removeChangeListener(realmListenerExams);
        realm.close();
    }

    /**
     * Empfänger für Updates der Noten vom Service
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final SwipeRefreshLayout swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));

            if (intent.getIntExtra(Const.IntentParams.BROADCAST_CODE, -1) == 0) {
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
