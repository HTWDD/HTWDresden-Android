package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.classes.TimetableRoomHelper;
import de.htwdd.htwdresden.types.LessonRoom;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;

/**
 * Adapter für die Anzeige des Belegungsplanes
 *
 * @author Kay Förster
 */
public class RoomTimetableAdapter extends RealmBaseAdapter<LessonRoom> {
    private static final String[] nameOfDays = Arrays.copyOfRange(DateFormatSymbols.getInstance().getWeekdays(), 2, 8);
    private final Realm realm;

    public RoomTimetableAdapter(@NonNull final Realm realm, @Nullable final OrderedRealmCollection<LessonRoom> data) {
        super(data);
        this.realm = realm;
    }

    @Override
    public View getView(final int i, @Nullable View view, @NonNull final ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        final ViewHolder viewHolder;

        // ViewHolder
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.fragment_room_timetable_item, viewGroup, false);
            view.setTag(viewHolder);
            viewHolder.overview = (LinearLayout) view.findViewById(R.id.overview_lessons_list);
            viewHolder.title = (TextView) view.findViewById(R.id.fragment_room_timetable_titel);
            viewHolder.day = (TextView) view.findViewById(R.id.overview_lesson_day);
        } else viewHolder = (ViewHolder) view.getTag();

        final LessonRoom lessonRoom = getItem(i);
        if (lessonRoom == null)
            return view;

        // Bestimme aktuelle DS
        final Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        int currentDs = TimetableHelper.getCurrentDS(TimetableHelper.getMinutesSinceMidnight(calendar));

        // Bestimme aktuellen Tag
        if (currentDs < 0) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            currentDs = 0;
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            currentDs = 0;
        }

        // Setze Title
        viewHolder.position = i;
        viewHolder.overview.removeAllViews();
        viewHolder.title.setText(lessonRoom.getRoom());
        viewHolder.day.setText(nameOfDays[calendar.get(Calendar.DAY_OF_WEEK) - 2]);

        // Übersicht des Belegungsplanes anzeigen
        TimetableRoomHelper.createSimpleLessonOverview(context, realm, viewHolder.overview, calendar, currentDs, lessonRoom.getRoom());

        return view;
    }

    private static class ViewHolder {
        int position;
        TextView title;
        TextView day;
        LinearLayout overview;
    }
}
