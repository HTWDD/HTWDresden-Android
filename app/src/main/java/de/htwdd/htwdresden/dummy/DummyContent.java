package de.htwdd.htwdresden.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.htwdd.htwdresden.adapter.MensaOverviewAdapter;
import de.htwdd.htwdresden.types.canteen.Canteen;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Canteen> ITEMS = new ArrayList<Canteen>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Canteen> ITEM_MAP = new HashMap<String, Canteen>();

    static {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Canteen> realmResults = realm.where(Canteen.class)
                .findAll();

        for (Canteen canteen : realmResults ) {
            addItem(canteen);
        }
    }

    private static void addItem(Canteen canteen) {
        ITEMS.add(canteen);
        ITEM_MAP.put(String.valueOf(canteen.getId()), canteen);
    }
}
