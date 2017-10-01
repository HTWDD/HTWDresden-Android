package de.htwdd.htwdresden.types.studyGroups;

import de.htwdd.htwdresden.interfaces.ISpinnerEntity;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;

/**
 * Übersicht über alle Studiengänge der HTW, Repräsentiert Studienkurse
 *
 * @author Kay Förster
 */
public class StudyCourse extends RealmObject implements ISpinnerEntity {
    private String studyCourse;
    private String name;
    private RealmList<StudyGroup> studyGroups;
    @LinkingObjects("studyCourses")
    private final RealmResults<StudyYear> studyYears = null;

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

    @Override
    public String getSpinnerName() {
        return studyCourse + " " + name;
    }
}
