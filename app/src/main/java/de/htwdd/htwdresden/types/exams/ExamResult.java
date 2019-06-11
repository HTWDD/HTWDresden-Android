package de.htwdd.htwdresden.types.exams;

import androidx.annotation.Nullable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Klasse für ein Prüfungsergebnis
 *
 * @author Kay Förster
 */
public class ExamResult extends RealmObject {
    @PrimaryKey
    public long id;
    /**
     * offizielle Prüfungsnummer
     */
    public int nr;
    /**
     * Semester in welchem die Prüfung durchgeführt wurde.
     * Jahr + Kennung des Semesters
     */
    @Index
    public Integer semester;
    /**
     * Datum an welchem die Prüfung stattfand
     */
    @Nullable
    public Date examDate;
    /**
     * Name der Prüfung
     */
    public String text;
    /**
     * Vermerk
     */
    @Nullable
    public String note;
    /**
     * Prüfungsnote
     */
    @Nullable
    public Float grade;
    /**
     * Status der Prüfung
     */
    @Nullable
    public String state;
    /**
     * Credits für diese Prüfung
     */
    public float credits;
    /**
     * Art der Prüfung (schriftlich, mündlich, etc)
     */
    public String form;
    /**
     * Versuch
     */
    public short tries;

    /**
     * Liefert die im Standardformat oder 0 wenn keine Note vorhanden ist
     *
     * @return Note im Standardformat oder 0.0 wenn keine Note vorhanden
     */
    public Float getGrade() {
        if (grade != null)
            return grade / 100;
        return 0f;
    }
}
