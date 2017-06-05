package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.interfaces.ILesson;
import io.realm.RealmModel;
import io.realm.RealmResults;

import static de.htwdd.htwdresden.classes.Const.Timetable.beginDS;
import static de.htwdd.htwdresden.classes.Const.Timetable.endDS;

/**
 * Stellt allgemeine Hilfsmethoden für Lehrveranstaltungen bereit
 *
 * @author Kay Förster
 */
abstract class AbstractTimetableHelper {
    final static DateFormat DATE_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);

    /**
     * Liefert die Minuten seit Mitternacht aus dem übergeben {@see Calendar}
     *
     * @param calendar Datum / Zeit
     * @return Minuten seit Mitternacht
     */
    public static long getMinutesSinceMidnight(@NonNull final Calendar calendar) {
        return TimeUnit.MINUTES.convert(calendar.get(Calendar.HOUR_OF_DAY), TimeUnit.HOURS) + calendar.get(Calendar.MINUTE);
    }

    /**
     * Liefert die aktuelle DS zur übergeben Zeit
     *
     * @param currentTime Minuten seit Mitternacht
     * @return die aktuelle DS, 0 falls vor der ersten DS oder -1 nach der letzten DS
     */
    public static int getCurrentDS(long currentTime) {
        if (currentTime >= endDS[6])
            return -1;
        else if (currentTime >= beginDS[6])
            return 7;
        else if (currentTime >= beginDS[5])
            return 6;
        else if (currentTime >= beginDS[4])
            return 5;
        else if (currentTime >= beginDS[3])
            return 4;
        else if (currentTime >= beginDS[2])
            return 3;
        else if (currentTime >= beginDS[1])
            return 2;
        else if (currentTime >= beginDS[0])
            return 1;

        return 0;
    }

    /**
     * Bestimmt ob übergebene Kalenderwoche gerade oder ungerade ist
     *
     * @param calendarWeek aktuelle Kalenderwoche
     * @return 1=ungerade KW, 2=gerade KW
     */
    static int getWeekTyp(final int calendarWeek) {
        final int result = calendarWeek % 2;
        return result == 0 ? 2 : result;
    }

    /**
     * Erstellt eine Liste von Lehrveranstaltungen des Tages
     *
     * @param context      aktueller App-Context
     * @param linearLayout {@link LinearLayout} in welchem die Liste eingefügt wird
     * @param current_ds   aktuelle DS welche hervorgehoben werden soll,sonst 0
     * @param <T>          {@link ILesson}
     */
    static <T extends RealmModel & ILesson> void createSimpleLessonOverview(@NonNull final Context context, @NonNull final List<RealmResults<T>> iLessons,
                                                                            @NonNull final LinearLayout linearLayout, final int current_ds) {
        final LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Resources resources = context.getResources();
        int iteration = 0;
        int foundedLessons;

        for (final RealmResults<T> lessons : iLessons) {
            final View sub_view = mLayoutInflater.inflate(R.layout.fragment_timetable_mini_plan, linearLayout, false);

            // Hintergrund einfärben
            if (iteration == (current_ds - 1))
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.timetable_blue));
            else if (iteration % 2 == 0)
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.app_background));
            else
                sub_view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

            // Zeiten anzeigen
            final TextView textDS = (TextView) sub_view.findViewById(R.id.timetable_busy_plan_ds);
            textDS.setText(resources.getString(
                    R.string.timetable_ds_list_simple,
                    DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.beginDS[iteration])),
                    DATE_FORMAT.format(Const.Timetable.getDate(Const.Timetable.endDS[iteration]))
            ));

            // Stunde anzeigen
            foundedLessons = lessons.size();
            final TextView textLesson = (TextView) sub_view.findViewById(R.id.timetable_busy_plan_lesson);
            switch (foundedLessons) {
                case 0:
                    textLesson.setText("");
                    break;
                case 1:
                    final ILesson lesson = lessons.first();
                    textLesson.setText(context.getResources().getString(R.string.timetable_overview_lessons, lesson.getLessonTag(), lesson.getType()));
                    break;
                default:
                    textLesson.setText(R.string.timetable_moreLessons);
                    break;
            }

            // View zum LinearLayout hinzufügen
            linearLayout.addView(sub_view);
            iteration++;
        }
    }
}
