package de.htwdd.htwdresden.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.interfaces.ILesson;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * abstracter Adapter für die Grid-Stundenplan-Ansicht
 *
 * @author Kay Förster
 */
abstract class AbstractTimetableGridAdapter<T extends RealmModel & ILesson> extends BaseAdapter {
    protected final Realm realm;
    protected final Calendar calendar;
    protected final int week;
    final boolean filterCurrentWeek;
    private final static String[] nameOfDays = DateFormatSymbols.getInstance().getShortWeekdays();
    private final static DateFormat DATE_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);
    private final static GridView.LayoutParams layoutParams_1 = new GridView.LayoutParams(GridView.AUTO_FIT, 50);
    private final static GridView.LayoutParams layoutParams_2 = new GridView.LayoutParams(GridView.AUTO_FIT, 180);

    AbstractTimetableGridAdapter(@NonNull final Realm realm, final int week, final boolean filterCurrentWeek) {
        this.realm = realm;
        this.calendar = GregorianCalendar.getInstance(Locale.GERMANY);
        this.week = week;
        this.filterCurrentWeek = filterCurrentWeek;
    }

    @Override
    public abstract RealmResults<T> getItem(final int i);

    @Override
    public int getCount() {
        return 63;
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    @Override
    public View getView(final int i, @Nullable View view, final ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        final ViewHolder viewHolder;

        // ViewHolder
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.fragment_timetable_grid_item, viewGroup, false);
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
        viewHolder.layout.setLayoutParams(layoutParams_2);

        switch (i) {
            case 0:
                viewHolder.type.setText(null);
                viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                viewHolder.layout.setLayoutParams(layoutParams_1);
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                viewHolder.type.setText(nameOfDays[i + 1]);
                viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                viewHolder.layout.setLayoutParams(layoutParams_1);
                break;
            case 7:
            case 14:
            case 21:
            case 28:
            case 35:
            case 42:
            case 49:
            case 56:
                viewHolder.type.setText(context.getResources().getString(
                        R.string.timetable_ds_grid,
                        DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.beginDS[(i / 7) - 1])),
                        DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.endDS[(i / 7) - 1]))
                ));
                viewHolder.type.setHeight(180);
                viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                break;
            default:
                viewHolder.tag.setVisibility(View.VISIBLE);
                viewHolder.room.setVisibility(View.VISIBLE);

                T lesson = null;
                final RealmResults<T> lessons = getItem(i);

                // Keine Lehrveranstaltung in diesem Zeitabschnitt
                if (lessons.size() == 0) {
                    viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
                    viewHolder.tag.setText(null);
                    viewHolder.type.setText(null);
                    viewHolder.room.setText(null);
                    break;
                }
                // Nur eine Lehrveranstaltung für diesem Zeitabschnitt
                else if (lessons.size() == 1) {
                    lesson = lessons.first();
                }
                // mehrere Lehrveranstaltungen für dieser DS vorhanden
                else if (lessons.size() > 1) {
                    int singleLesson = 0;
                    if (!filterCurrentWeek) {
                        // Wenn nicht nach einer entsprechenden Woche gesucht wird jetzt nach einer Lehrveranstaltung suchen, welche bevorzugt angezeigt werden kann
                        for (T lesson2 : lessons) {
                            final long inside = lesson2.getWeeksOnly()
                                    .where()
                                    .equalTo("weekOfYear", calendar.get(Calendar.WEEK_OF_YEAR))
                                    .count();

                            if (inside > 0 || lesson2.getWeeksOnly().isEmpty()) {
                                singleLesson++;
                                lesson = lesson2;
                            }
                        }
                    }

                    // Verdeutlichen das mehrere Lehrveranstaltung möglich sind
                    viewHolder.more.setVisibility(View.VISIBLE);

                    // Es gibt keine passende Veranstaltung die angezeigt werden kann
                    if (singleLesson != 1) {
                        viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
                        viewHolder.tag.setText(null);
                        viewHolder.room.setVisibility(View.GONE);
                        viewHolder.type.setText(R.string.timetable_moreLessons);
                        break;
                    }
                }

                if (lesson == null) {
                    break;
                }

                viewHolder.tag.setText(lesson.getLessonTag());
                viewHolder.room.setText(getLessonInfo(lesson));

                // Setze Hintergrundfarbe
                final String[] lessonType = view.getResources().getStringArray(R.array.lesson_type);
                switch (TimetableHelper.getIntegerTypOfLesson(lesson)) {
                    case Const.Timetable.TAG_VORLESUNG:
                        viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.timetable_blue));
                        viewHolder.type.setText(lessonType[0]);
                        break;
                    case Const.Timetable.TAG_PRAKTIKUM:
                        viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.timetable_organge));
                        viewHolder.type.setText(lessonType[1]);
                        break;
                    case Const.Timetable.TAG_UBUNG:
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

    @Nullable
    abstract String getLessonInfo(@NonNull final T lesson);

    private static class ViewHolder {
        int position;
        TextView tag;
        TextView type;
        TextView room;
        TextView more;
        TextView kw;
        LinearLayout layout;
    }
}
