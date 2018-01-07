package de.htwdd.htwdresden;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.adapter.MensaOverviewWeekAdapter;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.Meal;
import io.realm.Realm;


/**
 * Fragment welches die einzelnen Gerichte anzeigt
 *
 * @author Kay Förster
 */
public class MensaDetailWeekFragment extends Fragment implements IRefreshing {
    private SwipeRefreshLayout swipeRefreshLayout;
    private int modus = 1;
    private Realm realm;

    public MensaDetailWeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();

        // Inflate the layout for this fragment
        final View mLayout = inflater.inflate(R.layout.listview_swipe_refresh, container, false);

        // Überprüfe Bundle & setze Modus
        final Bundle bundle = getArguments();
        if (bundle != null) {
            modus = bundle.getInt(Const.BundleParams.MENSA_DETAIL_MODE, 1);
        }

        // Setze Swipe Refresh Layout
        swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            final Context context = getActivity();
            // Überprüfe Internetverbindung
            if (!VolleyDownloader.CheckInternet(context)) {
                onCompletion();
                Toast.makeText(context, R.string.info_no_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            final MensaHelper mensaHelper = new MensaHelper(context, (short) 9);
            mensaHelper.loadAndSaveMeals(modus);
        });

        // Setze Kalender auf Montag der ausgewählten Woche
        final Calendar beginOfWeek = GregorianCalendar.getInstance(Locale.GERMANY);
        beginOfWeek.set(Calendar.DAY_OF_WEEK, beginOfWeek.getFirstDayOfWeek());
        if (modus == 2) {
            beginOfWeek.roll(Calendar.WEEK_OF_YEAR, 1);
        }

        ((ListView) mLayout.findViewById(R.id.listView)).setAdapter(new MensaOverviewWeekAdapter(beginOfWeek, realm.where(Meal.class).findAll()));

        return mLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    @Override
    public void onCompletion() {
        if (!isDetached()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}