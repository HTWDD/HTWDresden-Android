package de.htwdd.htwdresden.classes;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Stellt Hilfsmethoden für das Semester bereit
 *
 * @author Kay Förster
 */
public class SemesterHelper {

    /**
     * Liefert das Jahr in welchem das Semester begann. Dies ist besonders im Wintersemester nach dem Jahreswechsel wichtig
     *
     * @return Jahr in welchem das aktuelle Semester begann
     */
    public static int getStartYearOfSemester() {
        final Calendar calendar = GregorianCalendar.getInstance();
        if (calendar.get(Calendar.MONTH) <= Calendar.FEBRUARY)
            return calendar.get(Calendar.YEAR) - 1;
        return calendar.get(Calendar.YEAR);
    }

    /**
     * Liefert für das aktuelle Semester den entsprechenden Tag
     *
     * @return Tag (W oder S) des aktuellen Semesters
     */
    public static String getActualSemesterTag() {
        final int month = GregorianCalendar.getInstance().get(Calendar.MONTH);
        if (month > Calendar.FEBRUARY && month < Calendar.SEPTEMBER)
            return "S";
        return "W";
    }
}
