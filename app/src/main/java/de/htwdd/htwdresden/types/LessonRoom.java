package de.htwdd.htwdresden.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.htwdd.htwdresden.interfaces.ILesson;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Beschreibt eine Lehrveranstaltung für den Raumbelegungsplan
 *
 * @author Kay Förster
 */
public class LessonRoom extends RealmObject implements ILesson {
    @PrimaryKey
    @Required
    private String id;
    private String lessonTag;
    private String name;
    private String type;
    @Index
    private int day;
    @Index
    private int week;
    private int beginTime;
    private int endTime;
    private String professor;
    private RealmList<LessonWeek> weeksOnly;
    @Required
    @Index
    private String room;
    private Date lastChanged;
    private RealmList<String> studyGroups;

    public String getId() {
        return id;
    }

    @Override
    public String getLessonTag() {
        return lessonTag;
    }

    public void setLessonTag(@Nullable final String lessonTag) {
        this.lessonTag = lessonTag;
    }

    public String getName() {
        return name;
    }

    public void setName(@Nullable final String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(@Nullable final String type) {
        this.type = type;
    }

    public int getDay() {
        return day;
    }

    public void setDay(final int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(final int week) {
        this.week = week;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(final int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(final int endTime) {
        this.endTime = endTime;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(@Nullable final String professor) {
        this.professor = professor;
    }

    @Override
    public RealmList<LessonWeek> getWeeksOnly() {
        return weeksOnly;
    }

    public void setWeeksOnly(@Nullable final RealmList<LessonWeek> weeksOnly) {
        this.weeksOnly = weeksOnly;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    @NonNull
    @Override
    public RealmList<String> getRooms() {
        return new RealmList<>();
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(@NonNull final String room) {
        this.room = room;
    }

    public RealmList<String> getStudyGroups() {
        return studyGroups;
    }
}
