package de.htwdd.htwdresden.types.studyGroups;

import android.support.annotation.NonNull;

import de.htwdd.htwdresden.interfaces.ISpinnerName;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Studienjahrgang
 *
 * @author Kay Förster
 */
public class StudyYear extends RealmObject implements ISpinnerName {
    public int studyYear;
    public RealmList<StudyCourse> studyCourses;

    @NonNull
    @Override
    public String getSpinnerSelect() {
        return String.valueOf(studyYear);
    }
}
