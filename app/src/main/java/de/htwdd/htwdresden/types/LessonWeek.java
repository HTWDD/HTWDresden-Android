package de.htwdd.htwdresden.types;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Repräsentiert eine Kalenderwoche für {@link Lesson2}
 *
 * @author Kay Förster
 */
public class LessonWeek extends RealmObject{
    @PrimaryKey
    private int weekOfYear;

    public int getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(final int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }
}
