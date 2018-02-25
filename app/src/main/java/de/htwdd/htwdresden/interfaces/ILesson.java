package de.htwdd.htwdresden.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.htwdd.htwdresden.types.LessonWeek;
import io.realm.RealmList;

/**
 * Beschreibung einer Lehrveranstaltung
 * @author Kay Förster
 */
public interface ILesson {
    @Nullable
    String getLessonTag();
    String getType();
    RealmList<LessonWeek> getWeeksOnly();
    @NonNull
    RealmList<String> getRooms();
}
