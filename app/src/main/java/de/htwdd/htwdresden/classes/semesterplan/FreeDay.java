package de.htwdd.htwdresden.classes.semesterplan;

/**
 * Created by warik on 04.03.16.
 */
public final class FreeDay extends  Period{
    private String NAME;

    @Override
    public String toString() {
        return "FreeDay{" +
                "NAME='" + NAME + '\'' +
                "} " + super.toString();
    }

    public String getNAME() {
        return NAME;
    }

    public String getBEGIN_DAY() {
        return super.getBEGIN_DAY();
    }

    public String getEND_DAY() {
        return super.getEND_DAY();
    }

    public FreeDay(String NAME, String BEGIN_DAY, String END_DAY) {
        super(BEGIN_DAY,END_DAY);
        this.NAME = NAME;
    }
}
