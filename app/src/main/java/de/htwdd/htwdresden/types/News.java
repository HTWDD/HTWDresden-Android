package de.htwdd.htwdresden.types;


import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Repräsentiert eine allgemeine News
 *
 * @author Kay Förster
 */
public class News {
    @SuppressWarnings("unused")
    private int plattform;
    @Nullable
    @SuppressWarnings("unused")
    private Date beginDay;
    @Nullable
    @SuppressWarnings("unused")
    private Date endDay;
    @SuppressWarnings("unused")
    private String title;
    @SuppressWarnings("unused")
    private String content;
    @Nullable
    @SuppressWarnings("unused")
    private String url;

    public int getPlattform() {
        return plattform;
    }

    @Nullable
    public Date getBeginDay() {
        return beginDay;
    }

    @Nullable
    public Date getEndDay() {
        return endDay;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Nullable
    public String getUrl() {
        return url;
    }
}