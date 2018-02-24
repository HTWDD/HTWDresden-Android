package de.htwdd.htwdresden.types.exams;

/**
 * Representation von einem Studienkurs
 *
 * @author Kay Förster
 */
public class Course {
    private String AbschlTxt;
    private int POVersion;
    private String AbschlNr;
    private String StgNr;
    private String StgTxt;

    public int getPOVersion() {
        return POVersion;
    }

    public String getAbschlNr() {
        return AbschlNr;
    }

    public String getStgNr() {
        return StgNr;
    }
}
