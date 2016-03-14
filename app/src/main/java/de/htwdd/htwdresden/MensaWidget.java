package de.htwdd.htwdresden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.nio.charset.Charset;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.types.Meal;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.classes.VolleyDownloader;

/**
 * Mensa-Widget Provider
 *
 * @author Kay Förster
 */
public class MensaWidget extends AppWidgetProvider {
    private static final String LOG_TAG = "MensaWidgetProvider";
    private static final String[] nameOfDays = DateFormatSymbols.getInstance().getWeekdays();

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        // Construct the RemoteViews object
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        final int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        final RemoteViews views = getRemoteViews(context, minHeight);

        // Erstelle Intent zum Starten der App
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Const.IntentParams.START_WITH_FRAGMENT, R.id.navigation_mensa);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT, intent, 0);
        views.setOnClickPendingIntent(R.id.mensa_widget_layout, pendingIntent);

        // Außerhalb der Geschäftszeiten wird nichts aktualisiert
        Calendar calendar = GregorianCalendar.getInstance();
        int hour_of_Day = calendar.get(Calendar.HOUR_OF_DAY);
        if ((hour_of_Day > 1 && hour_of_Day <= 10) || hour_of_Day >= 16 || calendar.get(Calendar.DAY_OF_WEEK) >= Calendar.SATURDAY) {
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        }
        views.setTextViewText(R.id.info_message, null);

        // Nur noch Informationen für den nächsten Tag anzeigen
        final int modus;
        final int day;
        if (calendar.get(Calendar.HOUR_OF_DAY) > 15) {
            if (calendar.get(Calendar.DAY_OF_WEEK) >= Calendar.FRIDAY) {
                modus = 2;
                day = Calendar.MONDAY;
                views.setTextViewText(R.id.widget_mensa_date, context.getString(R.string.mensa_date, nameOfDays[2]));
            } else {
                modus = 1;
                day = calendar.get(Calendar.DAY_OF_WEEK);
                views.setTextViewText(R.id.widget_mensa_date, context.getString(R.string.mensa_date, nameOfDays[calendar.get(Calendar.DAY_OF_WEEK)]));
            }
        }
        // Speiseplan von heute anzeigen
        else {
            modus = 0;
            day = 2;
            views.setTextViewText(R.id.widget_mensa_date, context.getString(R.string.mensa_date, context.getString(R.string.mensa_date_today)));
        }

        StringRequest stringRequest = new StringRequest(MensaHelper.getMensaUrl(modus), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Meal> meals;
                MensaHelper mensaHelper = new MensaHelper(context, (short) 9);
                try {
                    switch (modus) {
                        case 0:
                            meals = mensaHelper.parseCurrentDay(response);
                            break;
                        default:
                            response = new String(response.getBytes(Charset.forName("iso-8859-1")), Charset.forName("UTF-8"));
                            meals = mensaHelper.parseDayFromWeek(response, day);
                            break;
                    }
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.toString());
                    views.setTextViewText(R.id.info_message, context.getString(R.string.info_error_parse));
                    return;
                }

                // Daten bereinigen und aufarbeiten
                for (Iterator<Meal> iterator = meals.iterator(); iterator.hasNext(); ) {
                    Meal meal = iterator.next();
                    if (meal.getPrice().equals("ausverkauft") || meal.getTitle().matches(".*kombinierBAR:.*")) {
                        iterator.remove();
                        continue;
                    }

                    // Nur Preis für Studenten brücksichtigen
                    String price = meal.getPrice();
                    if (price.contains("/")) {
                        price = price.substring(0, price.lastIndexOf("/"));
                        price = price.replace("EUR", "€").trim();
                        meal.setPrice(price);
                    }
                }

                // Anzeigen der Gerichte
                Resources ressource = context.getResources();
                String packageName = context.getPackageName();
                int cells = minHeight <= 65 ? 4 : 8;
                for (int i = 1; i < cells + 1; i++) {
                    int mealName = ressource.getIdentifier("widget_mensa_item_meal_" + i, "id", packageName);
                    int mealPrice = ressource.getIdentifier("widget_mensa_item_price_" + i, "id", packageName);

                    if (meals.size() <= i) {
                        views.setTextViewText(mealName, null);
                        views.setTextViewText(mealPrice, null);
                        continue;
                    }

                    Meal meal = meals.get(i - 1);
                    views.setTextViewText(mealName, meal.getTitle());
                    views.setTextViewText(mealPrice, meal.getPrice());
                }

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }, null);

        // Request abfeuern
        VolleyDownloader.getInstance(context).addToRequestQueue(stringRequest);
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

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId);

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }


    /**
     * Determine appropriate view based on width provided.
     *
     * @param context aktueller App-Context
     * @param height  aktuelle Größe des Widgets in dp
     * @return RemoteView für die jeweilige Anzahl
     */
    private static RemoteViews getRemoteViews(@NonNull final Context context, int height) {
        if (Const.widget.getCellsForSize(height) == 1)
            return new RemoteViews(context.getPackageName(), R.layout.widget_mensa_list);
        else return new RemoteViews(context.getPackageName(), R.layout.widget_mensa_grid);
    }
}

