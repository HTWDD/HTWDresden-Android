package de.htwdd.htwdresden.classes;

import android.support.annotation.Nullable;

import java.util.Calendar;

import de.htwdd.htwdresden.types.LessonUser;
import io.realm.RealmResults;

/**
 * Beinhaltet das Suchergebnis nach einer zukünftigen Lehrveranstaltung
 */
public class NextLessonResult {
    @Nullable
    private Calendar startTimeOfLesson;
    @Nullable
    private RealmResults<LessonUser> results;

    void setResults(@Nullable final RealmResults<LessonUser> results) {
        this.results = results;
    }

    @Nullable
    public RealmResults<LessonUser> getResults() {
        return results;
    }

    @Nullable
    public Calendar getStartTimeOfLesson() {
        return startTimeOfLesson;
    }

    public void setStartTimeOfLesson(@Nullable final Calendar startTimeOfLesson) {
        this.startTimeOfLesson = startTimeOfLesson;
    }
}
