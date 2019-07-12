package de.htwdd.htwdresden;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.htwdd.htwdresden.dummy.DummyContent;
import de.htwdd.htwdresden.types.canteen.Canteen;

/**
 * A fragment representing a single Canteen detail screen.
 * @link CanteenDetailActivity}
 * on handsets.
 */
public class CanteenDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_CANTEEN_ID = "canteen_id";
    /**
     * The dummy content this fragment is presenting.
     */
    private Canteen mCanteen;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CanteenDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mCanteen = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_CANTEEN_ID));

            Toolbar appBarLayout = (Toolbar) this.getActivity().findViewById(R.id.detail_toolbar);
            if (appBarLayout != null) {
                appBarLayout.setTitle(trimName(mCanteen.getName()));
            }
    }

    private String trimName(String name) {
        String shortName = name.replaceAll("Dresden, ", "");
        shortName = shortName.replaceAll("Tharandt, ", "");

        return shortName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.canteen_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mCanteen != null) {
            ((TextView) rootView.findViewById(R.id.canteen_detail)).setText(mCanteen.getAddress());
        }

        return rootView;
    }
}
