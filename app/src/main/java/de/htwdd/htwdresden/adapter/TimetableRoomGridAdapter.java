package de.htwdd.htwdresden.adapter;

import android.support.annotation.NonNull;

import java.util.Calendar;

import de.htwdd.htwdresden.classes.TimetableRoomHelper;
import de.htwdd.htwdresden.types.LessonRoom;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Übersicht über Belegungsplan
 *
 * @author Kay Förster
 */
public class TimetableRoomGridAdapter extends AbstractTimetableGridAdapter<LessonRoom> {
    private final String room;

    public TimetableRoomGridAdapter(@NonNull final Realm realm, @NonNull final String room, final int week, final boolean filterCurrentWeek) {
        super(realm, week, filterCurrentWeek);
        this.room = room;
    }

    @Override
    public RealmResults<LessonRoom> getItem(final int i) {
        final int day = i % 7;
        calendar.set(Calendar.DAY_OF_WEEK, day + 1);
        calendar.set(Calendar.WEEK_OF_YEAR, week);

        return TimetableRoomHelper.getLessonsByDateAndDs(realm, calendar, room, (i - day) / 7, filterCurrentWeek);
    }

    @Override
    String getLessonInfo(@NonNull final LessonRoom lesson) {
        return TimetableRoomHelper.getStringOfStudyGroups(lesson);
    }
}
