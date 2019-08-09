package de.htwdd.htwdresden.types.studyGroups;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class StudyData extends RealmObject {

    @PrimaryKey
    private int id;
    private int studyYear;
    private String studyCourse;
    private String studyGroup;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(final int studyYear) {
        this.studyYear = studyYear;
    }

    public String getStudyCourse() {
        return studyCourse;
    }

    public void setStudyCourse(final String studyCourse) {
        this.studyCourse = studyCourse;
    }

    public String getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(final String studyGroup) {
        this.studyGroup = studyGroup;
    }
}
