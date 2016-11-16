package de.htwdd.htwdresden.types.studyGroups;

import android.support.annotation.NonNull;

import de.htwdd.htwdresden.interfaces.ISpinnerName;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Studiengang
 *
 * @author Kay Förster
 */
public class StudyCourse extends RealmObject implements ISpinnerName {
    // Studiengang
    public String studyCourse;
    public RealmList<StudyGroup> studyGroups;

    @NonNull
    @Override
    public String getSpinnerSelect() {
        return studyCourse;
    }
}
