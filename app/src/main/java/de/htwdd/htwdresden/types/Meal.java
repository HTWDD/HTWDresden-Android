package de.htwdd.htwdresden.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.classes.MensaHelper;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Meal extends RealmObject {
    @PrimaryKey
    private int id;
    private String title = "";
    private String price = "";
    @Index
    private short mensaId;
    @Index
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable final String title) {
        this.title = title;
    }

    @Nullable
    public String getPrice() {
        return price;
    }

    public void setPrice(@Nullable final String price) {
        this.price = price;
    }

    public void setDate(@NonNull final Calendar date) {
        this.date = MensaHelper.getDate(date);
    }

    public void setMensaId(final short mensaId) {
        this.mensaId = mensaId;
    }

    @NonNull
    public String getImageUrl() {
        if (date == null || id == 0)
            return "";

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return String.format(
                Locale.getDefault(),
                "https://bilderspeiseplan.studentenwerk-dresden.de/m%d/%d%02d/thumbs/%d.jpg",
                mensaId,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                id
        );
    }
}