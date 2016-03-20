package de.htwdd.htwdresden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import de.htwdd.htwdresden.classes.Const;

/**
 * Implementation of App Widget functionality.
 */
public class TimetableWidget extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_timetable);

        // Erstelle Intent zum Starten der App
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Const.IntentParams.START_ACTION_TIMETABLE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT, intent, 0);
        views.setOnClickPendingIntent(R.id.timetable_widget_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

