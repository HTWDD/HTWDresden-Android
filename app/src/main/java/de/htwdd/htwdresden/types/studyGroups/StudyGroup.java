package de.htwdd.htwdresden.types.studyGroups;

import de.htwdd.htwdresden.interfaces.ISpinnerEntity;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;

/**
 * Übersicht über alle Studiengänge der HTW, Repräsentiert Studiengruppe
 *
 * @author Kay Förster
 */
public class StudyGroup extends RealmObject implements ISpinnerEntity {
    private String studyGroup;
    private String name;
    private int grade;
    @LinkingObjects("studyGroups")
    private final RealmResults<StudyCourse> studyCourses = null;

    @Override
    public String getSpinnerName() {
        return studyGroup + " - " + name;
    }

    public String getStudyGroup() {
        return studyGroup;
    }

    public int getGrade() {
        return grade;
    }

    public RealmResults<StudyCourse> getStudyCourses() {
        return studyCourses;
    }
}
