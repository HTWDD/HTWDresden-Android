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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import de.htwdd.htwdresden.adapter.MensaOverviewAdapter;
import de.htwdd.htwdresden.classes.ConnectionHelper;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.canteen.Canteen;
import io.realm.Realm;
import io.realm.RealmResults;


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
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mLayout = inflater.inflate(R.layout.listview_swipe_refresh, container, false);
        realm = Realm.getDefaultInstance();

        // Suche Views
        final ListView listView = mLayout.findViewById(R.id.listView);
        swipeRefreshLayout = mLayout.findViewById(R.id.swipeRefreshLayout);
        ((TextView) mLayout.findViewById(R.id.message_info)).setText(R.string.mensa_no_offer);

        // Setze Swipe Refresh Layout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            final Context context = getContext();
            if (context == null) {
                return;
            }
            // Überprüfe Internetverbindung
            if (ConnectionHelper.checkNoInternetConnection(context)) {
                onCompletion();
                Toast.makeText(context, R.string.info_no_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            final MensaHelper mensaHelper = new MensaHelper(context, (short) 1);
            mensaHelper.updateCanteens(this);
        });

        // Setze Adapter
        final RealmResults<Canteen> realmResults = realm.where(Canteen.class)
                .findAll();
        final MensaOverviewAdapter mensaArrayAdapter = new MensaOverviewAdapter(realmResults);
        listView.setAdapter(mensaArrayAdapter);
        listView.setEmptyView(mLayout.findViewById(R.id.message_info));
        // Default Divider
        final TypedArray typedArray = mLayout.getContext().obtainStyledAttributes(new int[]{ android.R.attr.listDivider });
        //listView.setDivider(typedArray.getDrawable(0));
        listView.setDividerHeight(8);
        typedArray.recycle();

        listView.setOnItemClickListener(((adapterView, view, i, l) -> {
            final Canteen canteen = mensaArrayAdapter.getItem(i);
            if (canteen != null) {

                Context context = view.getContext();
                Intent intent = new Intent(context, CanteenDetailActivity.class);
                intent.putExtra(CanteenDetailFragment.ARG_CANTEEN_ID, String.valueOf(canteen.getId()));

                context.startActivity(intent);
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