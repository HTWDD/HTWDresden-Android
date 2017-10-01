package de.htwdd.htwdresden.classes;

import android.support.annotation.NonNull;

import de.htwdd.htwdresden.types.studyGroups.StudyGroup;

/**
 * Stellt Hilfsmethoden für die Studiengruppe zur Verfügung
 *
 * @author Kay Förster
 */
public class StudyGroupHelper {

    public static char getGraduationChar(@NonNull final StudyGroup studyGroup) {
        switch (studyGroup.getGrade()) {
            case 0:
            case 2:
                return 'D';
            case 5:
            case 6:
                return 'B';
            case 7:
            case 8:
                return 'M';
            default:
                return 'U';
        }
    }
}
