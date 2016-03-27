package de.htwdd.htwdresden.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;

import de.htwdd.htwdresden.classes.Const;


/**
 * Ergebniss-Objekt einer Suche über Stunden
 *
 * @author Kay Förster
 */
public class LessonSearchResult {
    private Lesson lesson;
    private int code;
    private String timeRemaining;
    private Calendar calendar;

    public LessonSearchResult() {
        this.code = Const.Timetable.NO_LESSON_FOUND;
        this.timeRemaining = "";
    }

    public void setTimeRemaining(final String timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setLesson(final Lesson lesson) {
        this.lesson = lesson;
    }

    public void setCalendar(final Calendar calendar) {
        this.calendar = calendar;
    }

    @Nullable
    public Lesson getLesson() {
        return lesson;
    }

    public int getCode() {
        return code;
    }

    @NonNull
    public String getTimeRemaining() {
        return timeRemaining;
    }

    @Nullable
    public Calendar getCalendar() {
        return calendar;
    }
}
