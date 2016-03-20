package de.htwdd.htwdresden;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.service.TimetableWidgetService;

/**
 * Stundenplan-Widget Provider
 */
public class TimetableWidget extends AppWidgetProvider {
    private static PendingIntent pendingIntent = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Starte Service zum Updaten des Widget
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, TimetableWidgetService.class);
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (pendingIntent == null)
            pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES), pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        if (pendingIntent != null) {
            Log.d("TimetableWidget", "onDisabler");
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}

