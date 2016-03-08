package de.htwdd.htwdresden.types.semesterplan;

/**
 * Class to store Periods
 * Created by Vitali Drazdovich , Artyom Dyadechkin
 */
public class Period {

    private String BEGIN_DAY;
    private String END_DAY;
    private StringBuilder dateBild;

    public Period(String BEGIN_DAY, String END_DAY) {
        this.BEGIN_DAY = BEGIN_DAY;
        this.END_DAY = END_DAY;
    }

    @Override
    public String toString() {
        return formDate(BEGIN_DAY) + " - " + formDate(END_DAY);
    }

    public String getBEGIN_DAY() {
        return BEGIN_DAY;
    }

    public String getEND_DAY() {
        return END_DAY;
    }

    public String formDate(String date) {
        dateBild = new StringBuilder();
        dateBild.append((String) date.subSequence(8,10));
        dateBild.append(".");
        dateBild.append((String) date.subSequence(5,7));
        dateBild.append(".");
        dateBild.append((String) date.subSequence(2,4));
        return dateBild.toString();
    }
}
