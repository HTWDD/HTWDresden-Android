package de.htwdd.htwdresden.types;

import android.support.annotation.Nullable;

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
     * Veröffentlichungsdatum der Prüfungsergebnisse
     */
    @Nullable
    public Date publicDate;
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
    public short trail;
}
