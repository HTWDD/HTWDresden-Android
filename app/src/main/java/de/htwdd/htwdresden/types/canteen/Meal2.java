package de.htwdd.htwdresden.types.canteen;

import androidx.annotation.Nullable;

import java.util.Date;

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
    private Date date;
    private Prices prices;
    @Nullable
    private short mensaId;

    public long getId() { return id; }
    public void setId(long value) { this.id = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public String getCategory() { return category; }

    public Date getDate() { return date; }
    public void setDate(Date date) {
        this.date = date;
    }

    public RealmList<String> getNotes() { return notes; }

    public Prices getPrices() { return prices; }

    public void setMensaId(final short mensaId) {
        this.mensaId = mensaId;
    }
}

