package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Arrays;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.types.LessonUser;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Adapter um Stunden in einer Liste anzeigen
 *
 * @author Kay Förster
 */
public class TimetableListAdapter extends RealmBaseAdapter<LessonUser> {
    private static final String[] nameOfDays = Arrays.copyOfRange(DateFormatSymbols.getInstance().getWeekdays(), 2, 8);
    private final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    private final String[] lessonWeek;
    private final String[] lessonType;

    public TimetableListAdapter(@NonNull final Context context, @Nullable final OrderedRealmCollection<LessonUser> data) {
        super(data);
        final Resources resources = context.getResources();
        lessonType = resources.getStringArray(R.array.lesson_type);
        lessonWeek = resources.getStringArray(R.array.lesson_week);
    }

    @Override
    public View getView(final int i, @Nullable View view, final ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        final Context context = viewGroup.getContext();

        // ViewHolder
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.timetable_list_item, viewGroup, false);
            view.setTag(viewHolder);

            viewHolder.lesson_name = view.findViewById(R.id.timetable_edit_lessonName);
            viewHolder.lesson_typ = view.findViewById(R.id.timetable_edit_lessonType);
            viewHolder.lesson_room = view.findViewById(R.id.timetable_edit_lessonRooms);
            viewHolder.lesson_kw = view.findViewById(R.id.timetable_edit_lessonWeek);
            viewHolder.lesson_day = view.findViewById(R.id.timetable_edit_lessonDay);
            viewHolder.lesson_ds = view.findViewById(R.id.timetable_edit_lessonDS);
            viewHolder.lesson_weeksOnly = view.findViewById(R.id.timetable_edit_lessonWeeksOnly);
        } else viewHolder = (ViewHolder) view.getTag();

        final LessonUser lesson = getItem(i);
        if (lesson == null)
            return view;


        viewHolder.lesson_room.setText(TimetableHelper.getStringOfRooms(lesson));
        viewHolder.lesson_kw.setText(lessonWeek[lesson.getWeek()]);
        viewHolder.lesson_day.setText(nameOfDays[lesson.getDay() - 1]);
        viewHolder.lesson_weeksOnly.setText(TimetableHelper.getStringOfKws(lesson));
        if (lesson.getLessonTag() != null) {
            viewHolder.lesson_name.setText(context.getString(R.string.timetable_details_title, lesson.getLessonTag(), lesson.getName()));
        } else {
            viewHolder.lesson_name.setText(lesson.getName());
        }
        if (lesson.getProfessor() != null && !lesson.getProfessor().isEmpty()) {
            viewHolder.lesson_typ.setText(context.getString(R.string.timetable_details_subtitle, lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)], lesson.getProfessor()));
        } else {
            viewHolder.lesson_typ.setText(lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)]);
        }

        viewHolder.lesson_ds.setText(context.getString(
                R.string.timetable_ds_list_simple,
                dateFormat.format(Const.Timetable.getDate(lesson.getBeginTime())),
                dateFormat.format(Const.Timetable.getDate(lesson.getEndTime()))
        ));

        return view;
    }

    private static class ViewHolder {
        TextView lesson_name;
        TextView lesson_typ;
        TextView lesson_room;
        TextView lesson_kw;
        TextView lesson_day;
        TextView lesson_ds;
        TextView lesson_weeksOnly;
    }
}
