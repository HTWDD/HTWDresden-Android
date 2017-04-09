package de.htwdd.htwdresden.classes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;

import de.htwdd.htwdresden.types.Lesson2;
import io.realm.RealmResults;

/**
 * Beinhaltet das Suchergebnis nach einer zukünftigen Lehrveranstaltung
 */
public class NextLessonResult {
    private Calendar onNextDay;
    @Nullable
    private RealmResults<Lesson2> results;

    NextLessonResult(@NonNull final Calendar onNextDay) {
        this.onNextDay = onNextDay;
    }

    void setResults(@Nullable final RealmResults<Lesson2> results) {
        this.results = results;
    }

    public Calendar getOnNextDay() {
        return onNextDay;
    }

    @Nullable
    public RealmResults<Lesson2> getResults() {
        return results;
    }
}
