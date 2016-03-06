package de.htwdd.htwdresden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.htwdd.htwdresden.classes.Const;

/**
 * Implementation of App Widget functionality.
 */
public class MensaWidget extends AppWidgetProvider {
    private static final String LOG_TAG = "MensaWidgetProvider";
    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mensa_widget);
        views.setTextViewText(R.id.appwidget_text, format.format(GregorianCalendar.getInstance().getTime()));

        // Erstelle Intent zum Starten der App
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Const.IntentParams.START_WITH_FRAGMENT, R.id.navigation_mensa);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.mensa_widget_layout, pendingIntent);

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

