package de.htwdd.htwdresden.types.studyGroups;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Übersicht über alle Studiengänge der HTW, Repräsentiert Studienjahr
 *
 * @author Kay Förster
 */
public class StudyYear extends RealmObject {
    @PrimaryKey
    private int studyYear;
    private RealmList<StudyCourse> studyCourses;

    public int getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(final int studyYear) {
        this.studyYear = studyYear;
    }

    public RealmList<StudyCourse> getStudyCourses() {
        return studyCourses;
    }

    public void setStudyCourses(final RealmList<StudyCourse> studyCourses) {
        this.studyCourses = studyCourses;
    }
}
