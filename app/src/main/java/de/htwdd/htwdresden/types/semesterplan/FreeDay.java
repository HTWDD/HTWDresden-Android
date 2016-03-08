package de.htwdd.htwdresden.types.semesterplan;

/**
 * Class to store FreeDays
 * Created by Vitali Drazdovich , Artyom Dyadechkin
 */
public final class FreeDay extends Period {
    private String NAME;

    @Override
    public String toString() {
        return super.toString() + "\n";
    }

    public String getNAME() {
        return NAME + "\n";
    }

    public String getBEGIN_DAY() {
        return super.getBEGIN_DAY();
    }

    public String getEND_DAY() {
        return super.getEND_DAY();
    }

    public FreeDay(String NAME, String BEGIN_DAY, String END_DAY) {
        super(BEGIN_DAY, END_DAY);
        this.NAME = NAME;
    }
}