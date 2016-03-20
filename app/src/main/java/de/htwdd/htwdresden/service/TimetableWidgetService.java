package de.htwdd.htwdresden.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import java.util.Date;

import de.htwdd.htwdresden.MainActivity;
import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.TimetableWidget;
import de.htwdd.htwdresden.classes.Const;

/**
 * Service zum regelmäßigen Updaten des Stundenplan Widgets
 *
 * @author Kay Förster
 */
public class TimetableWidgetService extends Service {

    private void updateWidget() {
        Context context = getApplicationContext();

        String lastUpdated = DateFormat.format("hh:mm:ss", new Date()).toString();

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget_timetable);
        view.setTextViewText(R.id.widget_timetable_lesson_next, lastUpdated);

        // Erstelle Intent zum Starten der App
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Const.IntentParams.START_ACTION_TIMETABLE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT, intent, 0);
        view.setOnClickPendingIntent(R.id.timetable_widget_layout, pendingIntent);

        // Update das Widget
        ComponentName thisWidget = new ComponentName(this, TimetableWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);
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
