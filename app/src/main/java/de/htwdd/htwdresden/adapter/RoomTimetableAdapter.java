package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.LessonHelper;
import de.htwdd.htwdresden.types.Lesson;
import de.htwdd.htwdresden.types.RoomTimetable;

/**
 * Adapter für die Belungsanzeige
 *
 * @author Kay Förster
 */
public class RoomTimetableAdapter extends BaseAdapter {
    private static final String[] nameOfDays = Arrays.copyOfRange(DateFormatSymbols.getInstance().getWeekdays(), 2, 8);
    private static final String[] listOfDs = new String[Const.Timetable.beginDS.length];
    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private ArrayList<RoomTimetable> roomTimetables;
    private LayoutInflater mLayoutInflater;
    private Context context;
    private Calendar calendar;

    public RoomTimetableAdapter(@NonNull Context context, @NonNull ArrayList<RoomTimetable> roomTimetables) {
        this.roomTimetables = roomTimetables;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.calendar = GregorianCalendar.getInstance();

        // DS-Spinner mit Daten füllen
        int count = listOfDs.length;
        Resources resources = context.getResources();
        for (int i = 0; i < count; i++)
            listOfDs[i] = resources.getString(R.string.timetable_ds_list_simple, format.format(Const.Timetable.beginDS[i]), format.format(Const.Timetable.endDS[i]));
    }

    @Override
    public int getCount() {
        return roomTimetables.size();
    }

    @Override
    public RoomTimetable getItem(int i) {
        return roomTimetables.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        final RoomTimetable roomTimetable = getItem(i);

        // ViewHolder
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.fragment_room_timetable_item, viewGroup, false);
            view.setTag(viewHolder);
            viewHolder.title = (TextView) view.findViewById(R.id.fragment_room_timetable_titel);
            viewHolder.day = (TextView) view.findViewById(R.id.overview_lesson_day);
        } else viewHolder = (ViewHolder) view.getTag();

        // Setze Title
        viewHolder.title.setText(roomTimetable.roomName);

        // Bestimme aktuelle DS
        int current_ds = Const.Timetable.getCurrentDS(null);

        // Bestimme Tag
        if (roomTimetable.day != calendar.get(Calendar.DAY_OF_WEEK) - 1) {
            viewHolder.day.setText(nameOfDays[roomTimetable.day - 1]);
            // Anzeige der aktuellen Stunde "ausschalten"
            current_ds = 0;
        }

        // Daten für Stundenplan-Vorschau
        final String[] values = new String[7];

        for (int x = 1; x < 8; x++) {
            ArrayList<Lesson> lessons = new ArrayList<>();

            // Suche nach Stunden für die aktuelle DS
            for (Lesson lesson : roomTimetable.timetable)
                if (lesson.getDs() == x)
                    lessons.add(lesson);

            // Suche nach passender Stunde
            int single = ViewHolder.lessonHelper.searchLesson(lessons, calendar.get(Calendar.WEEK_OF_YEAR));

            switch (single) {
                case 0:
                    values[x - 1] = "";
                    break;
                case 1:
                    values[x - 1] = ViewHolder.lessonHelper.lesson.getTag() + " (" + ViewHolder.lessonHelper.lesson.getType() + ")";
                    break;
                case 2:
                    values[x - 1] = view.getResources().getString(R.string.timetable_moreLessons);
                    break;
            }
        }

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.overview_lessons_list);
        linearLayout.removeAllViews();

        // Tagesüberblick anzeigen
        int index = 0;
        for (String lessonDs : listOfDs) {
            View sub_view = mLayoutInflater.inflate(R.layout.fragment_timetable_mini_plan, viewGroup, false);

            // Hintergrund einfärben
            if (index == (current_ds - 1))
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.timetable_blue));
            else if (index % 2 == 0)
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.app_background));
            else
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

            // Zeiten anzeigen
            TextView textDS = (TextView) sub_view.findViewById(R.id.timetable_busy_plan_ds);
            textDS.setText(lessonDs);

            // Stunde anzeigen
            TextView textLesson = (TextView) sub_view.findViewById(R.id.timetable_busy_plan_lesson);
            textLesson.setText(values[index]);

            // View zum LinearLayout hinzufügen
            linearLayout.addView(sub_view);

            // Index erhöhen
            index++;
        }

        viewHolder.position = i;

        return view;
    }

    static class ViewHolder {
        public int position;
        public TextView title;
        public TextView day;
        public static LessonHelper lessonHelper = new LessonHelper();
    }
}
