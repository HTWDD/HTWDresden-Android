package de.htwdd.htwdresden.types.canteen;

import android.support.annotation.Nullable;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Beschreibung einer Mahlzeit
 *
 * @author Kay Förster
 */
public class Meal extends RealmObject {
    @Nullable
    private Float studentPrice;
    @Nullable
    private Float employeePrice;
    private String title;
    private boolean isSoldOut;
    private RealmList<String> additives;
    private RealmList<String> information;
    private RealmList<String> allergens;
    private Date date;
    private String detailURL;
    @Nullable
    private String image;
    private short mensaId;

    @Nullable
    public Float getStudentPrice() {
        return studentPrice;
    }

    @Nullable
    public Float getEmployeePrice() {
        return employeePrice;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public RealmList<String> getAdditives() {
        return additives;
    }

    public RealmList<String> getInformation() {
        return information;
    }

    public RealmList<String> getAllergens() {
        return allergens;
    }

    public Date getDate() {
        return date;
    }

    public String getDetailURL() {
        return detailURL;
    }

    @Nullable
    public String getImage() {
        return image;
    }

    public void setMensaId(final short mensaId) {
        this.mensaId = mensaId;
    }
}
