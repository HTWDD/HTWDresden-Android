package de.htwdd.htwdresden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.RemoteViews;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.types.canteen.Meal2;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Mensa-Widget Provider
 *
 * @author Kay Förster
 */
public class MensaWidget extends AppWidgetProvider {
    private final static String[] nameOfDays = DateFormatSymbols.getInstance().getWeekdays();

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        // Construct the RemoteViews object
        final Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        final int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        final RemoteViews views = getRemoteViews(context, minHeight);
        final Calendar calendar = GregorianCalendar.getInstance();

        // Erstelle Intent zum Starten der App
        final Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Const.IntentParams.START_ACTION_MENSA);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT, intent, 0);
        views.setOnClickPendingIntent(R.id.mensa_widget_layout, pendingIntent);

        // Bestimme Tag, welcher angezeigt werden soll
        views.setTextViewText(R.id.widget_mensa_date, context.getString(R.string.mensa_date, context.getString(R.string.mensa_date_today)));

        if (calendar.get(Calendar.HOUR_OF_DAY) > 15) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            views.setTextViewText(R.id.widget_mensa_date, context.getString(R.string.mensa_date, nameOfDays[calendar.get(Calendar.DAY_OF_WEEK)]));
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            // Ersten Tag in der Woche berechnen
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            views.setTextViewText(R.id.widget_mensa_date, context.getString(R.string.mensa_date, nameOfDays[2]));
        }

        // Lade Daten aus der Datenbank
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Meal2> meals = realm.where(Meal2.class)
                .equalTo(Const.database.Canteen.MENSA_DATE, MensaHelper.getDate(calendar))
                .equalTo(Const.database.Canteen.MENSA_IS_SOLDOUT, false)
                .findAll();

        // Anzeigen der Gerichte
        final Resources ressource = context.getResources();
        final String packageName = context.getPackageName();
        final int cells = minHeight <= 65 ? 4 : 8;
        Meal2 meal;

        for (int i = 1; i < cells + 1; i++) {
            final int mealName = ressource.getIdentifier("widget_mensa_item_meal_" + i, "id", packageName);
            final int mealPrice = ressource.getIdentifier("widget_mensa_item_price_" + i, "id", packageName);

            if (meals.size() < i) {
                views.setTextViewText(mealName, null);
                views.setTextViewText(mealPrice, null);
                continue;
            }

            // Gericht anzeigen
            meal = meals.get(i - 1);
            if (meal == null){
                continue;
            }

            views.setTextViewText(mealName, meal.getName());
            views.setTextViewText(mealPrice, context.getString(R.string.mensa_euro, meal.getPrices().getStudents()));
        }

        // Info setzen
        views.setTextViewText(R.id.info_message, meals.size() == 0 ? ressource.getText(R.string.mensa_no_offer) : null);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        realm.close();
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        final Calendar calendar = GregorianCalendar.getInstance();
        final int hour_of_Day = calendar.get(Calendar.HOUR_OF_DAY);

        // Während der Mensa Öffnungszeiten, Speisepläne vorher aktualisieren
        if (hour_of_Day >= 10 && hour_of_Day <= 15 && calendar.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY && calendar.get(Calendar.DAY_OF_WEEK) < Calendar.SATURDAY) {
            final MensaHelper mensaHelper = new MensaHelper(context, (short) 1);
            mensaHelper.updateMeals(() -> {
                // Widgets updaten
                for (final int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
            });
            return;
        }

        // There may be multiple widgets active, so update all of them
        for (final int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(final Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(final Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onAppWidgetOptionsChanged(final Context context,final AppWidgetManager appWidgetManager, int appWidgetId,final Bundle newOptions) {
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

