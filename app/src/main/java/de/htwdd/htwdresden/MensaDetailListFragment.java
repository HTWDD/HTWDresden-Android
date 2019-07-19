package de.htwdd.htwdresden;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.htwdd.htwdresden.adapter.MensaOverviewAdapter;
import de.htwdd.htwdresden.classes.ConnectionHelper;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.canteen.Canteen;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static de.htwdd.htwdresden.MealDetailListFragment.ARG_CANTEEN_ID;
import static de.htwdd.htwdresden.classes.Const.database.Canteen.MENSA_ID;


/**
 * Fragment welches die einzelnen Mensen anzeigt
 *
 * @author Kay Förster
 */
public class MensaDetailListFragment extends Fragment implements IRefreshing {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Realm realm;

    public MensaDetailListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MensaHelper mensaHelper = new MensaHelper(Objects.requireNonNull(getContext()), (short) 80);
        mensaHelper.updateCanteens(this);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Canteen> canteenList = realm.where(Canteen.class).findAll();

        for (Canteen canteen : canteenList) {

            short mensaId =  (short) canteen.getId();

            MensaHelper mensaHelperMeals = new MensaHelper(getContext(), mensaId);
            mensaHelperMeals.updateWeekMeals(this);
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mLayout = inflater.inflate(R.layout.listview_swipe_refresh, container, false);
        realm = Realm.getDefaultInstance();

        // Suche Views
        final ListView listView = mLayout.findViewById(R.id.listView);
        swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        ((TextView) mLayout.findViewById(R.id.message_info)).setText(R.string.mensa_no_offer);

        swipeRefreshLayout.setEnabled(false);

        // Setze Adapter
        final RealmResults<Canteen> realmResults = realm.where(Canteen.class)
                .findAll().sort("isFav", Sort.DESCENDING);


        final MensaOverviewAdapter mensaArrayAdapter = new MensaOverviewAdapter(realmResults);
        listView.setAdapter(mensaArrayAdapter);
        listView.setEmptyView(mLayout.findViewById(R.id.message_info));
        final TypedArray typedArray = mLayout.getContext().obtainStyledAttributes(new int[]{ android.R.attr.listDivider });
        listView.setDividerHeight(50);
        typedArray.recycle();

        listView.setOnItemClickListener(((adapterView, view, i, l) -> {
            final Canteen canteen = mensaArrayAdapter.getItem(i);
            if (canteen != null) {
                final FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                Fragment fragment = new MealFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ARG_CANTEEN_ID, String.valueOf(canteen.getId()));

                fragment.setArguments(bundle);
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.activity_main_FrameLayout, fragment, null).commitAllowingStateLoss();
            }
        }));

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