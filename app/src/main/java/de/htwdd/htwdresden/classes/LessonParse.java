package de.htwdd.htwdresden.classes;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import de.htwdd.htwdresden.types.Lesson;

/**
 * @author Kay Förster
 */
public class LessonParse {

    /**
     * Erstellt aus einem JSON-Array von Stunden eine Liste von Stunden-Objekten
     *
     * @param response JSON-Array welches die Stunden enthält
     * @return Liste mit Stunden-Objekten
     * @throws JSONException
     * @throws CloneNotSupportedException
     */
    public static ArrayList<Lesson> getList(@NonNull JSONArray response) throws JSONException, CloneNotSupportedException {
        int count = response.length();
        ArrayList<Lesson> lessons = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Lesson lesson = new Lesson();
            lesson.parseFromJSON(response.getJSONObject(i));
            lessons.add(lesson);

            // Bestimme End DS und ggf neu eintragen
            switch (lesson.getDs()) {
                case 1:
                    if (lesson.getEndTime().before(Const.Timetable.beginDS[1]))
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(2);
                    lessons.add(lesson);
                case 2:
                    if (lesson.getEndTime().before(Const.Timetable.beginDS[2]))
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(3);
                    lessons.add(lesson);
                case 3:
                    if (lesson.getEndTime().before(Const.Timetable.beginDS[3]))
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(4);
                    lessons.add(lesson);
                case 4:
                    if (lesson.getEndTime().before(Const.Timetable.beginDS[4]))
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(5);
                    lessons.add(lesson);
                case 5:
                    if (lesson.getEndTime().before(Const.Timetable.beginDS[5]))
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(6);
                    lessons.add(lesson);
                case 6:
                    if (lesson.getEndTime().before(Const.Timetable.beginDS[6]))
                        break;
                    lesson = lesson.clone();
                    lesson.setDs(7);
                    lessons.add(lesson);
            }
        }
        return lessons;
    }
}
