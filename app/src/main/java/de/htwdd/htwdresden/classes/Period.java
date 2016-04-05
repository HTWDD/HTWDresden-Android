package de.htwdd.htwdresden.classes;

import android.support.annotation.NonNull;

/**
 * Klasse zum speichern von Perioden
 *
 * @author Vitali Drazdovich, Artyom Dyadichkin
 */
public class Period {
    private final String beginDay;
    private final String endDay;

    public Period(@NonNull final String beginDay, @NonNull final String endDay) {
        this.beginDay = beginDay;
        this.endDay = endDay;
    }

    @NonNull
    public String getBeginDay() {
        return beginDay;
    }

    @NonNull
    public String getEndDay() {
        return endDay;
    }
}
