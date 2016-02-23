package de.htwdd.htwdresden;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import de.htwdd.htwdresden.adapter.ExamStatsAdapter;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.ExamResultDAO;
import de.htwdd.htwdresden.events.UpdateExamResultsEvent;
import de.htwdd.htwdresden.types.ExamStats;

/**
 * Fragment zur Anzeige der Statistik der Prüfungsergebnisse
 */
public class ExamResultStatsFragment extends Fragment {
    private View mLayout;
    private ExamStatsAdapter adapter;
    private ArrayList<ExamStats> examStatses = new ArrayList<>();

    public ExamResultStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.listview_swipe_refresh, container, false);
        adapter = new ExamStatsAdapter(getActivity(), examStatses);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);

        ListView listView = (ListView) mLayout.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // Lade Daten
        loadData();

        return mLayout;
    }

    /**
     * Behandelt die Benachrichtigung vom Eventbus das neue Prüfungsergebnisse zur Verfügung stehen
     *
     * @param updateExamResultsEvent Typ der Benachrichtigung
     */
    @Subscribe
    public void updateExamResults(UpdateExamResultsEvent updateExamResultsEvent){
        loadData();
    }

    private void loadData() {
        final TextView message = (TextView) mLayout.findViewById(R.id.message_info);
        final ExamResultDAO dao = new ExamResultDAO(new DatabaseManager(getActivity()));

        examStatses.clear();
        examStatses.addAll(dao.getStats());
        adapter.notifyDataSetChanged();

        // Meldung anzeigen
        if (examStatses.size() == 0) {
            message.setText(R.string.exams_result_no_results);
            mLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_background));
        } else message.setText(null);
    }
}
