package de.htwdd.htwdresden.types;

import android.content.ContentValues;

import de.htwdd.htwdresden.interfaces.IGetContentValues;
import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Klasse für ein Prüfungsergebnis
 *
 * @author Kay Förster
 */
public class ExamResult extends RealmObject implements IGetContentValues {
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

    @Override
    public ContentValues getContentValues() {
        return new ContentValues();
    }
}
