package de.htwdd.htwdresden.classes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

import de.htwdd.htwdresden.types.LessonUser;
import io.realm.RealmResults;

/**
 * Beinhaltet das Suchergebnis nach einer zukünftigen Lehrveranstaltung
 */
public class NextLessonResult {
    private Calendar onNextDay;
    @Nullable
    private RealmResults<LessonUser> results;

    NextLessonResult(@NonNull final Calendar onNextDay) {
        this.onNextDay = onNextDay;
    }

    void setResults(@Nullable final RealmResults<LessonUser> results) {
        this.results = results;
    }

    public Calendar getOnNextDay() {
        return onNextDay;
    }

    @Nullable
    public RealmResults<LessonUser> getResults() {
        return results;
    }
}
