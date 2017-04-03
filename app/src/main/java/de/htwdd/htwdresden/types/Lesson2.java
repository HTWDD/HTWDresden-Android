package de.htwdd.htwdresden.types;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Beschreibt eine Lehrveranstaltung
 *
 * @author Kay Förster
 */
public class Lesson2 extends RealmObject {
    @PrimaryKey
    @Required
    private String id;
    private String moduleId;
    private String lessonTag;
    private String name;
    private String type;
    private int day;
    private int week;
    private int beginTime;
    private int endTime;
    private String professor;
    private RealmList<LessonWeek> weeksOnly;
    private RealmList<Room> rooms;
    private Date lastChanged;
    private boolean editedByUser;

    public String getId() {
        return id;
    }

    public String getLessonTag() {
        return lessonTag;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public RealmList<Room> getRooms() {
        return rooms;
    }

    public Date getLastChanged() {
        return lastChanged;
    }
}
