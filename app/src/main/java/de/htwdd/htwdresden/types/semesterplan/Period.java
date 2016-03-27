package de.htwdd.htwdresden.types.semesterplan;

/**
 * Class to store Periods
 * Created by Vitali Drazdovich , Artyom Dyadichkin
 */
public class Period {

    private String beginDay;
    private String endDay;

    public Period(String beginDay, String endDay) {
        this.beginDay = beginDay;
        this.endDay = endDay;
    }

    @Override
    public String toString() {
        if (beginDay.equals(endDay)) return formDate(beginDay);
        else return formDate(beginDay) + " - " + formDate(endDay);
    }

    public String getBeginDay() {
        return beginDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public String formDate(String date) {
        StringBuilder dateBild;
        dateBild = new StringBuilder();
        dateBild.append((String) date.subSequence(8,10));
        dateBild.append(".");
        dateBild.append((String) date.subSequence(5,7));
        dateBild.append(".");
        dateBild.append((String) date.subSequence(2,4));
        return dateBild.toString();
    }
}
