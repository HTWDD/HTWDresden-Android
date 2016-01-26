package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.Lesson;

/**
 * Adapter für die Grid-Stundenplan-Ansicht
 *
 * @author Kay Förster
 */
public class TimetableGridAdapter extends BaseAdapter {
    private Context context;
    private int week;
    private ArrayList<Lesson> lessons_week;
    private LayoutInflater mLayoutInflater;
    private static String[] lessonType;
    private static final String[] nameOfDays = DateFormatSymbols.getInstance().getShortWeekdays();
    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public TimetableGridAdapter(Context context, ArrayList<Lesson> lessons_week, int week) {
        this.context = context;
        this.week = week;
        this.lessons_week = lessons_week;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lessonType = context.getResources().getStringArray(R.array.lesson_type);
    }

    @Override
    public int getCount() {
        return 56;
    }

    @Override
    public ArrayList<Lesson> getItem(int i) {
        int ds = i / 7;
        int day = i % 7;

        ArrayList<Lesson> lessons = new ArrayList<>();

        for (Lesson lesson : lessons_week)
            if (lesson.getDay() == day && lesson.getDs() == ds)
                lessons.add(lesson);

        return lessons;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        ArrayList<Lesson> lessons;

        // ViewHolder
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.fragment_timetable_grid_item, viewGroup, false);
            view.setTag(viewHolder);
            viewHolder.type = (TextView) view.findViewById(R.id.timetableType);
            viewHolder.tag = (TextView) view.findViewById(R.id.timetableTag);
            viewHolder.room = (TextView) view.findViewById(R.id.timetableRoom);
            viewHolder.more = (TextView) view.findViewById(R.id.timetableMoreLessons);
            viewHolder.kw = (TextView) view.findViewById(R.id.timetableOnlyKW);
            viewHolder.layout = (LinearLayout) view.findViewById(R.id.timetableLayout);
        } else viewHolder = (ViewHolder) view.getTag();

        // Standardmäßig alles ausblenden
        viewHolder.tag.setVisibility(View.GONE);
        viewHolder.room.setVisibility(View.GONE);
        viewHolder.more.setVisibility(View.GONE);
        viewHolder.kw.setVisibility(View.GONE);

        // Standardgröße
        viewHolder.layout.setLayoutParams(viewHolder.layoutParams_2);

        switch (i) {
            case 0:
                viewHolder.type.setText(null);
                viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                viewHolder.layout.setLayoutParams(viewHolder.layoutParams_1);
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                viewHolder.type.setText(nameOfDays[i + 1]);
                viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                viewHolder.layout.setLayoutParams(viewHolder.layoutParams_1);
                break;
            case 7:
            case 14:
            case 21:
            case 28:
            case 35:
            case 42:
            case 49:
                viewHolder.type.setText(context.getResources().getString(R.string.timetable_ds_grid, format.format(Const.Timetable.beginDS[(i / 7) - 1]), format.format(Const.Timetable.endDS[(i / 7) - 1])));
                viewHolder.type.setHeight(180);
                viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                break;
            default:
                viewHolder.tag.setVisibility(View.VISIBLE);
                viewHolder.room.setVisibility(View.VISIBLE);

                Lesson lesson = null;
                lessons = getItem(i);

                // Nur eine Stunde für dieser DS vorhanden
                if (lessons.size() == 1) {
                    lesson = lessons.get(0);

                    viewHolder.tag.setText(lesson.getTag());
                    viewHolder.room.setText(lesson.getRooms());

                    if (!lesson.getWeeksOnly().isEmpty()) {
                        viewHolder.kw.setVisibility(View.VISIBLE);
                    }
                }
                // mehrere Stunden für dieser DS vorhanden
                else if (lessons.size() > 1) {
                    int single = 0;
                    viewHolder.more.setVisibility(View.VISIBLE);

                    // Suche nach einer passenden Veranstaltung
                    for (Lesson tmp : lessons) {
                        // Es ist keine spezielle KW gesetzt, d.h. die Veranstaltung ist immer
                        if (tmp.getWeeksOnly().isEmpty()) {
                            single++;

                            if (single == 1)
                                lesson = tmp;
                            else
                                // Zweite Veranstallung gefunden, die "immer" ist
                                break;
                        }

                        // Es sind spezielle KW gestzt, suche aktuelle zum anzeigen
                        String[] lessonWeek = tmp.getWeeksOnly().split(";");

                        // Aktuelle Woche enthalten?
                        if (Arrays.asList(lessonWeek).contains(week + "")) {
                            single++;

                            if (single == 1)
                                lesson = tmp;
                            else
                                // Zweite Veranstallung gefunden, die "immer" ist
                                break;
                        }
                    }

                    // Es gibt keine passende Veranstaltung die angezeigt werden kann
                    if (single != 1) {
                        viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
                        viewHolder.tag.setText(null);
                        viewHolder.room.setVisibility(View.GONE);
                        viewHolder.type.setText(R.string.timetable_moreLessons);
                        break;
                    }

                    // Doch eine Veranstalltung gefunden
                    viewHolder.tag.setText(lesson.getTag());
                    viewHolder.room.setText(lesson.getRooms());
                }
                // Keine Stunde in dieser DS
                else {
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
                    viewHolder.tag.setText(null);
                    viewHolder.type.setText(null);
                    viewHolder.room.setText(null);
                    break;
                }

                // Setze Hintergrundfarbe
                switch (lesson.getTypeInt()) {
                    case 0:
                        viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.timetable_blue));
                        viewHolder.type.setText(lessonType[0]);
                        break;
                    case 1:
                        viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.timetable_organge));
                        viewHolder.type.setText(lessonType[1]);
                        break;
                    case 2:
                        viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.timetable_green));
                        viewHolder.type.setText(lessonType[2]);
                        break;
                    default:
                        viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.timetable_red));
                        viewHolder.type.setText(lessonType[3]);
                        break;
                }
                break;
        }

        viewHolder.position = i;

        return view;
    }

    static class ViewHolder {
        public int position;
        public TextView tag;
        public TextView type;
        public TextView room;
        public TextView more;
        public TextView kw;
        public LinearLayout layout;
        public final GridView.LayoutParams layoutParams_1 = new GridView.LayoutParams(GridView.AUTO_FIT, 50);
        public final GridView.LayoutParams layoutParams_2 = new GridView.LayoutParams(GridView.AUTO_FIT, 180);
    }
}
