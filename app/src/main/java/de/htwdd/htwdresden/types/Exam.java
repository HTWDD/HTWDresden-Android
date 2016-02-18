package de.htwdd.htwdresden.types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.htwdd.htwdresden.interfaces.IParseJSON;

/**
 * Einfache Klasse für Prüfungen,
 * Aus Performancegründen und der Einfachheit wird auf Getter/Setter verzichtet
 */
public class Exam implements IParseJSON {
    public String day;
    public String endTime;
    public String examType;
    public String examiner;
    public String nextChance;
    public String rooms;
    public String startTime;
    public String studyBranch;
    public String title;

    @Override
    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString("Title");
        examType = jsonObject.getString("ExamType");
        studyBranch = jsonObject.getString("StudyBranch");
        day = jsonObject.getString("Day");
        startTime = jsonObject.getString("StartTime");
        endTime = jsonObject.getString("EndTime");
        examiner = jsonObject.getString("Examiner");
        nextChance = jsonObject.getString("NextChance");
        rooms = "";

        JSONArray arrayRooms = jsonObject.getJSONArray("Rooms");
        int countRooms = arrayRooms.length();

        for (int i = 0; i < countRooms; i++) {
            rooms += arrayRooms.getString(i);

            if (i < countRooms - 1)
                rooms += ", ";
        }
    }
}
