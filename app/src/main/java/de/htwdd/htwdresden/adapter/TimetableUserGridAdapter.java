package de.htwdd.htwdresden.adapter;

import android.support.annotation.NonNull;

import java.util.Calendar;

import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.types.LessonUser;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Adapter für die Grid-Stundenplan-Ansicht
 *
 * @author Kay Förster
 */
public class TimetableUserGridAdapter extends AbstractTimetableGridAdapter<LessonUser> {
    private final boolean showHiddenLessons;

    public TimetableUserGridAdapter(@NonNull final Realm realm, final int week, final boolean filterCurrentWeek, final boolean showHiddenLessons) {
        super(realm, week, filterCurrentWeek);
        this.showHiddenLessons = showHiddenLessons;
    }

    @Override
    public RealmResults<LessonUser> getItem(final int i) {
        final int day = i % 7;
        calendar.set(Calendar.DAY_OF_WEEK, day + 1);
        calendar.set(Calendar.WEEK_OF_YEAR, week);

        return TimetableHelper.getLessonsByDateAndDs(realm, calendar, (i - day) / 7, filterCurrentWeek, showHiddenLessons);
    }

    @Override
    String getLessonInfo(@NonNull final LessonUser lesson) {
        return TimetableHelper.getStringOfRooms(lesson);
    }
}
