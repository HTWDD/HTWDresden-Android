package de.htwdd.htwdresden.types;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.interfaces.IGetContentValues;
import de.htwdd.htwdresden.interfaces.IParseJSON;

/**
 * Beschreibung einer Lehrveranstaltung
 *
 * @author Kay Förster
 */
public class Lesson implements IParseJSON, IGetContentValues, Cloneable {
    private long id;
    private String tag;
    private String name;
    private String type;
    private int week;
    private int day;
    private int ds;
    private Time endTime;
    private String professor;
    private String weeksOnly;
    private String rooms;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getTypeInt() {
        if (type.contains("V"))
            return 0;
        else if (type.contains("Pr"))
            return 1;
        else if (type.contains("Ü"))
            return 2;
        else return 3;
    }

    public void setTypeInt(int value) {
        switch (value) {
            case 0:
                type = "V";
                break;
            case 1:
                type = "Pr";
                break;
            case 2:
                type = "Ü";
                break;
            default:
                type = "sonst.";
                break;
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getWeek() {
        return week;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public void setDs(int ds) {
        this.ds = ds;
    }

    public int getDs() {
        return ds;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getProfessor() {
        return professor;
    }

    public void setWeeksOnly(String weeksOnly) {
        this.weeksOnly = weeksOnly;
    }

    public String getWeeksOnly() {
        return weeksOnly;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getRooms() {
        return rooms;
    }

    @Override
    public Lesson clone() throws CloneNotSupportedException {
        return (Lesson) super.clone();
    }

    @Override
    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        tag = jsonObject.getString("lessonTag");
        name = jsonObject.getString("name");
        type = jsonObject.getString("type");
        week = jsonObject.getInt("week");
        day = jsonObject.getInt("day");
        Time beginTime = Time.valueOf(jsonObject.getString("beginTime"));
        endTime = Time.valueOf(jsonObject.getString("endTime"));
        professor = jsonObject.getString("professor");
        weeksOnly = jsonObject.getString("WeeksOnly");

        // Räume in einfachen String konventieren
        rooms = "";
        JSONArray roomArray = jsonObject.getJSONArray("Rooms");
        int roomArrayCount = roomArray.length();
        for (int i = 0; i < roomArrayCount; i++)
            rooms += roomArray.getString(i).replaceAll(" ", "") + " ";

        // Bestimme DS
        if (beginTime.before(Const.Timetable.endDS[0]))
            ds = 1;
        else if (beginTime.before(Const.Timetable.endDS[1]))
            ds = 2;
        else if (beginTime.before(Const.Timetable.endDS[2]))
            ds = 3;
        else if (beginTime.before(Const.Timetable.endDS[3]))
            ds = 4;
        else if (beginTime.before(Const.Timetable.endDS[4]))
            ds = 5;
        else if (beginTime.before(Const.Timetable.endDS[5]))
            ds = 6;
        else if (beginTime.before(Const.Timetable.endDS[6]))
            ds = 7;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.database.TimetableEntry.COLUMN_NAME_LESSONTAG, tag);
        contentValues.put(Const.database.TimetableEntry.COLUMN_NAME_NAME, name);
        contentValues.put(Const.database.TimetableEntry.COLUMN_NAME_TYP, type);
        contentValues.put(Const.database.TimetableEntry.COLUMN_NAME_WEEK, week);
        contentValues.put(Const.database.TimetableEntry.COLUMN_NAME_DAY, day);
        contentValues.put(Const.database.TimetableEntry.COLUMN_NAME_DS, ds);
        contentValues.put(Const.database.TimetableEntry.COLUMN_NAME_PROFESSOR, professor);
        contentValues.put(Const.database.TimetableEntry.COLUMN_NAME_WEEKSONLY, weeksOnly);
        contentValues.put(Const.database.TimetableEntry.COLUMN_NAME_ROOMS, rooms);
        return contentValues;
    }
}
