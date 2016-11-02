package de.htwdd.htwdresden.types.studyGroups;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Studienjahrgang
 *
 * @author Kay Förster
 */
public class StudyYear extends RealmObject {
    public int studyYear;
    public RealmList<StudyGroup> studyCourses;
}
