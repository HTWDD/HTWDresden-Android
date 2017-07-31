package de.htwdd.htwdresden.types.studyGroups;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Übersicht über alle Studiengänge der HTW, Repräsentiert Studienkurse
 *
 * @author Kay Förster
 */
public class StudyCourse extends RealmObject {
    private String studyCourse;
    private String name;
    private RealmList<StudyGroup> studyGroups;

    public String getStudyCourse() {
        return studyCourse;
    }

    public void setStudyCourse(final String studyCourse) {
        this.studyCourse = studyCourse;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public RealmList<StudyGroup> getStudyGroups() {
        return studyGroups;
    }

    public void setStudyGroups(final RealmList<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }
}
