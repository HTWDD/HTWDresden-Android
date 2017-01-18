package de.htwdd.htwdresden;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import de.htwdd.htwdresden.adapter.ExamStatsAdapter;
import de.htwdd.htwdresden.types.ExamResult;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Fragment zur Anzeige der Statistik der Prüfungsergebnisse
 */
public class ExamResultStatsFragment extends Fragment {
    private View mLayout;

    public ExamResultStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.listview_swipe_refresh, container, false);

        // Refresh ausschalten
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);

        // Adapter erstellen und an Liste anhängen
        final ExamStatsAdapter adapter = new ExamStatsAdapter(getActivity());
        final ListView listView = (ListView) mLayout.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // Daten aus Datenbank laden
        final Realm realm = Realm.getDefaultInstance();
        final long countExamResults = realm.where(ExamResult.class).count();

        // Auf Änderungen an der Datenbank hören
        realm.where(ExamResult.class).findAll().addChangeListener(new RealmChangeListener<RealmResults<ExamResult>>() {
            @Override
            public void onChange(final RealmResults<ExamResult> element) {
                adapter.notifyDataSetChanged();
                showMessageNoResults(element.size() > 0);
            }
        });
        showMessageNoResults(countExamResults > 0);

        return mLayout;
    }

    /**
     * Hinweismeldung anzeigen wenn keine Noten vorhanden sind
     *
     * @param examsAvailable Sind Noten vorhanden?
     */
    private void showMessageNoResults(final boolean examsAvailable) {
        final TextView message = (TextView) mLayout.findViewById(R.id.message_info);

        if (examsAvailable) {
            message.setText(null);
            mLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_background));
        } else {
            message.setText(R.string.exams_result_no_results);
            mLayout.setBackgroundColor(Color.WHITE);
        }
    }
}
