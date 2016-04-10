package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.Lesson;

/**
 * Adapter um Stunden in einer Liste anzeigen
 *
 * @author Kay Förster
 */
public class TimetableListAdapter extends BaseAdapter {
    private static final String[] nameOfDays = Arrays.copyOfRange(DateFormatSymbols.getInstance().getWeekdays(), 2, 8);
    private static final String[] listOfDs = new String[Const.Timetable.beginDS.length];
    private static String[] lesson_week;
    private static String[] lesson_typ;
    private Context context;
    private ArrayList<Lesson> lessons;
    private LayoutInflater mLayoutInflater;

    public TimetableListAdapter(Context context, ArrayList<Lesson> lessons) {
        this.context = context;
        this.lessons = lessons;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

        // DS-Spinner mit Daten füllen
        int count = listOfDs.length;
        Resources resources = context.getResources();
        for (int i = 0; i < count; i++)
            listOfDs[i] = resources.getString(
                    R.string.timetable_ds_list,
                    i + 1,
                    dateFormat.format(Const.Timetable.getDate(Const.Timetable.beginDS[i])),
                    dateFormat.format(Const.Timetable.getDate(Const.Timetable.endDS[i]))
            );

        lesson_week = resources.getStringArray(R.array.lesson_week);
        lesson_typ = resources.getStringArray(R.array.lesson_type);
    }

    @Override
    public int getCount() {
        return lessons.size();
    }

    @Override
    public Lesson getItem(int i) {
        return lessons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return lessons.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;

        // ViewHolder
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.timetable_list_item, viewGroup, false);
            view.setTag(viewHolder);

            viewHolder.lesson_name = (TextView) view.findViewById(R.id.timetable_edit_lessonName);
            viewHolder.lesson_typ = (TextView) view.findViewById(R.id.timetable_edit_lessonType);
            viewHolder.lesson_room = (TextView) view.findViewById(R.id.timetable_edit_lessonRooms);
            viewHolder.lesson_kw = (TextView) view.findViewById(R.id.timetable_edit_lessonWeek);
            viewHolder.lesson_day = (TextView) view.findViewById(R.id.timetable_edit_lessonDay);
            viewHolder.lesson_ds = (TextView) view.findViewById(R.id.timetable_edit_lessonDS);
            viewHolder.lesson_weeksOnly = (TextView) view.findViewById(R.id.timetable_edit_lessonWeeksOnly);
        } else viewHolder = (ViewHolder) view.getTag();

        final Lesson lesson = getItem(i);
        viewHolder.lesson_name.setText(context.getString(R.string.timetable_details_title, lesson.getTag(), lesson.getName()));
        if (lesson.getProfessor() != null && !lesson.getProfessor().isEmpty())
            viewHolder.lesson_typ.setText(context.getString(R.string.timetable_details_subtitle, lesson_typ[lesson.getTypeInt()], lesson.getProfessor()));
        else viewHolder.lesson_typ.setText(lesson_typ[lesson.getTypeInt()]);
        viewHolder.lesson_room.setText(lesson.getRooms());
        viewHolder.lesson_kw.setText(lesson_week[lesson.getWeek()]);
        viewHolder.lesson_day.setText(nameOfDays[lesson.getDay() - 1]);
        viewHolder.lesson_ds.setText(listOfDs[lesson.getDs() - 1]);
        viewHolder.lesson_weeksOnly.setText(lesson.getWeeksOnly());

        return view;
    }

    static class ViewHolder {
        public TextView lesson_name;
        public TextView lesson_typ;
        public TextView lesson_room;
        public TextView lesson_kw;
        public TextView lesson_day;
        public TextView lesson_ds;
        public TextView lesson_weeksOnly;
    }
}
