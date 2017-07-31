package de.htwdd.htwdresden.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import de.htwdd.htwdresden.MainActivity;
import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.TimetableWidget;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.NextLessonResult;
import de.htwdd.htwdresden.classes.TimetableHelper;
import de.htwdd.htwdresden.types.LessonUser;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Service zum regelmäßigen Updaten des Stundenplan Widgets
 *
 * @author Kay Förster
 */
public class TimetableWidgetService extends Service {

    private void updateWidget() {
        final RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget_timetable);
        final Context context = getApplicationContext();
        final Realm realm = Realm.getDefaultInstance();
        final String[] lessonType = getResources().getStringArray(R.array.lesson_type);

        // Aktuelle Stunde anzeigen
        final RealmResults<LessonUser> currentLesson = TimetableHelper.getCurrentLessons(realm);
        switch (currentLesson.size()) {
            case 0:
                view.setTextViewText(R.id.widget_timetable_lesson_time, null);
                view.setTextViewText(R.id.widget_timetable_lesson, null);
                view.setTextViewText(R.id.widget_timetable_room, null);
                break;
            case 1:
                final LessonUser lesson = currentLesson.first();
                view.setTextViewText(R.id.widget_timetable_lesson_time, TimetableHelper.getStringRemainingTime(context));
                view.setTextViewText(R.id.widget_timetable_lesson, lesson.getName());
                if (lesson.getRooms().size() > 0)
                    view.setTextViewText(R.id.widget_timetable_room, getString(
                            R.string.timetable_ds_list_simple,
                            lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)],
                            TimetableHelper.getStringOfRooms(lesson)
                    ));
                else
                    view.setTextViewText(R.id.widget_timetable_room, lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)]);
                break;
            default:
                view.setTextViewText(R.id.widget_timetable_lesson_time, TimetableHelper.getStringRemainingTime(context));
                view.setTextViewText(R.id.widget_timetable_lesson, getString(R.string.timetable_moreLessons));
                view.setTextViewText(R.id.widget_timetable_room, null);
                break;
        }

        // Nächste Stunde anzeigen
        final NextLessonResult nextLessonResult = TimetableHelper.getNextLessons(realm);
        if (nextLessonResult.getResults() == null || nextLessonResult.getResults().size() == 0) {
            view.setTextViewText(R.id.widget_timetable_lesson_time_next, null);
            view.setTextViewText(R.id.widget_timetable_lesson_next, null);
            view.setTextViewText(R.id.widget_timetable_room_next, null);
        } else if (nextLessonResult.getResults().size() == 1) {
            final LessonUser lesson = nextLessonResult.getResults().first();
            view.setTextViewText(R.id.widget_timetable_lesson_time_next, TimetableHelper.getStringStartNextLesson(context, nextLessonResult));
            view.setTextViewText(R.id.widget_timetable_lesson_next, lesson.getName());
            if (lesson.getRooms().size() > 0) {
                view.setTextViewText(R.id.widget_timetable_room_next, getString(
                        R.string.timetable_ds_list_simple,
                        lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)],
                        TimetableHelper.getStringOfRooms(lesson)
                ));
            } else {
                view.setTextViewText(R.id.widget_timetable_room_next, lessonType[TimetableHelper.getIntegerTypOfLesson(lesson)]);
            }
        } else {
            view.setTextViewText(R.id.widget_timetable_lesson_time_next, TimetableHelper.getStringStartNextLesson(context, nextLessonResult));
            view.setTextViewText(R.id.widget_timetable_lesson_next, getString(R.string.timetable_moreLessons));
            view.setTextViewText(R.id.widget_timetable_room_next, null);
        }
        realm.close();

        // Erstelle Intent zum Starten der App
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Const.IntentParams.START_ACTION_TIMETABLE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT, intent, 0);
        view.setOnClickPendingIntent(R.id.timetable_widget_layout, pendingIntent);

        // Update das Widget
        AppWidgetManager.getInstance(this).updateAppWidget(new ComponentName(this, TimetableWidget.class), view);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWidget();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
