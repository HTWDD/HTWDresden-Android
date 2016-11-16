package de.htwdd.htwdresden.types.studyGroups;

import android.support.annotation.NonNull;

import de.htwdd.htwdresden.interfaces.ISpinnerName;
import io.realm.RealmObject;

/**
 * Studiengruppe
 *
 * @author Kay Förster
 */
public class StudyGroup extends RealmObject implements ISpinnerName {
    public String studyGroup;

    @NonNull
    @Override
    public String getSpinnerSelect() {
        return studyGroup;
    }
}
