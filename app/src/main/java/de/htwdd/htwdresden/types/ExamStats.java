package de.htwdd.htwdresden.types;

import android.support.annotation.Nullable;

/**
 * Statistik für Prüfungsleistungen
 *
 * @author Kay Förster
 */
public class ExamStats {
    @Nullable
    public Integer semester;
    public float credits;
    public float gradeBest;
    public float gradeWorst;
    public int gradeCount;
    public float average;
}
