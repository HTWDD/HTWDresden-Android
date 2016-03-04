package de.htwdd.htwdresden.classes.semesterplan;

/**
 * Created by warik on 04.03.16.
 */
public class Period{
    public Period(String BEGIN_DAY, String END_DAY) {
        this.BEGIN_DAY = BEGIN_DAY;
        this.END_DAY = END_DAY;
    }

    @Override
    public String toString() {
        return "Period{" +
                "BEGIN_DAY='" + BEGIN_DAY + '\'' +
                ", END_DAY='" + END_DAY + '\'' +
                '}';
    }

    public String getBEGIN_DAY() {
        return BEGIN_DAY;
    }

    public String getEND_DAY() {
        return END_DAY;
    }

    private String BEGIN_DAY;
    private String END_DAY;
}
