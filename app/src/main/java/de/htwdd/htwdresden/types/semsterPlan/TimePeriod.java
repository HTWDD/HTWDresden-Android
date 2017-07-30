package de.htwdd.htwdresden.types.semsterPlan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Zeiteinteilung für {@link de.htwdd.htwdresden.types.semsterPlan.Semester}
 *
 * @author Kay Förster
 */
public class TimePeriod extends RealmObject {
    @Nullable
    private String name;
    private Date beginDay;
    private Date endDay;

    @Nullable
    public String getName() {
        return name;
    }

    @NonNull
    public Date getBeginDay() {
        return beginDay;
    }

    @NonNull
    public Date getEndDay() {
        return endDay;
    }
}
