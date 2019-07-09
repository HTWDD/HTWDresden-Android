package de.htwdd.htwdresden.types.canteen;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Beschreibung einer Mahlzeit
 *
 * @author Kay Förster
 */
public class Meal2 extends RealmObject {
    private long id;
    private String name;
    private String category;
    private RealmList<String> notes;
    private Prices prices;

    public long getId() { return id; }
    public void setId(long value) { this.id = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public String getCategory() { return category; }

    public RealmList<String> getNotes() { return notes; }

    public Prices getPrices() {
        return prices; }
}

