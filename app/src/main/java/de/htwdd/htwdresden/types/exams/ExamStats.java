package de.htwdd.htwdresden.types.exams;

import android.support.annotation.Nullable;

/**
 * Statistik für Prüfungsleistungen
 *
 * @author Kay Förster
 */
public class ExamStats {
    @Nullable
    public Integer semester;
    private float credits;
    private float gradeBest;
    private float gradeWorst;
    public long gradeCount;
    private double average;

    public float getGradeBest() {
        return gradeBest;
    }

    public void setGradeBest(final float gradeBest) {
        this.gradeBest = gradeBest;
        if (gradeBest > 0f)
            this.gradeBest /= 100;
    }

    public float getGradeWorst() {
        return gradeWorst;
    }

    public void setGradeWorst(final float gradeWorst) {
        this.gradeWorst = gradeWorst;
        if (gradeWorst > 0f)
            this.gradeWorst /= 100;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(final double average) {
        this.average = average;
        if (average > 0)
            this.average /= 100;
    }

    public float getCredits() {
        return credits;
    }

    public void setCredits(final float credits) {
        this.credits = credits;
    }
}
