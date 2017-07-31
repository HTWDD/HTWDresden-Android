package de.htwdd.htwdresden.types.studyGroups;

import io.realm.RealmObject;

/**
 * Übersicht über alle Studiengänge der HTW, Repräsentiert Studiengruppe
 *
 * @author Kay Förster
 */
public class StudyGroup extends RealmObject {
    private String studyGroup;
    private String name;
    private int grade;
}
