package de.htwdd.htwdresden.types.studyGroups;

import de.htwdd.htwdresden.interfaces.ISpinnerEntity;
import io.realm.RealmObject;

/**
 * Übersicht über alle Studiengänge der HTW, Repräsentiert Studiengruppe
 *
 * @author Kay Förster
 */
public class StudyGroup extends RealmObject implements ISpinnerEntity {
    private String studyGroup;
    private String name;
    private int grade;

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
}
