package de.htwdd.htwdresden.types;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Klasse für ein Prüfungsergebnis
 *
 * @author Kay Förster
 */
public class ExamResult extends RealmObject {
    @Index
    public Integer Semester;
    /**
     * Beschreibt den Modulnamen
     */
    public String PrTxt;
    public Float PrNote;
    public String Vermerk;
    public String Status;
    public Float EctsCredits;
    public Short Versuch;
    public String PrForm;
}
