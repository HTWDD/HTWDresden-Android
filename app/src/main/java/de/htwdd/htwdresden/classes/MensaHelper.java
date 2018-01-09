package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.htwdd.htwdresden.classes.API.ICanteenService;
import de.htwdd.htwdresden.classes.API.Retrofit2Client;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Stellt Funktionen zum Parsen der Mensa-Webseite bereit.
 *
 * @author Kay Förster
 */
public class MensaHelper {
    final private static String LOG_TAG = "MensaHelper";
    final private Context context;
    final private short mensaId;

    public MensaHelper(@NonNull final Context context, final short mensaId) {
        this.context = context;
        this.mensaId = mensaId;
    }

    /**
     * Aktualisiert Speisen der ausgewählten Mensa
     *
     * @param iRefreshing Callback welches nach Abschluss aufgerufen wird
     */
    public void updateMeals(@NonNull final IRefreshing iRefreshing) {
        updateMeals(iRefreshing, () -> {
        });
    }

    /**
     * Aktualisiert Speisen der ausgewählten Mensa
     * @param iRefreshing Callback welches nach Abschluss aufgerufen wird
     * @param successFinish Callback welches nach erfolgreichen Abschluss aufgerufen wird
     */
    public void updateMeals(@NonNull final IRefreshing iRefreshing, @NonNull final IRefreshing successFinish) {
        final ICanteenService canteenService = Retrofit2Client.getInstance(context).getRetrofit().create(ICanteenService.class);
        final Call<List<de.htwdd.htwdresden.types.canteen.Meal>> mealCall = canteenService.listMeals(String.valueOf(mensaId));
        mealCall.enqueue(new Callback<List<de.htwdd.htwdresden.types.canteen.Meal>>() {
            @Override
            public void onResponse(@NonNull final Call<List<de.htwdd.htwdresden.types.canteen.Meal>> call, @NonNull final retrofit2.Response<List<de.htwdd.htwdresden.types.canteen.Meal>> response) {
                Log.d(LOG_TAG, "Mensa Request erfolgreich");
                final List<de.htwdd.htwdresden.types.canteen.Meal> meals = response.body();
                if (meals != null) {
                    saveMeals(meals);
                }

                // Refreshing ausschalten
                iRefreshing.onCompletion();

                successFinish.onCompletion();
            }

            @Override
            public void onFailure(@NonNull final Call<List<de.htwdd.htwdresden.types.canteen.Meal>> call, @NonNull final Throwable t) {
                Log.e(LOG_TAG, "Fehler beim Abrufen der API", t);
                // Refreshing ausschalten
                iRefreshing.onCompletion();
            }
        });
    }

    private void saveMeals(@NonNull final List<de.htwdd.htwdresden.types.canteen.Meal> meals) {
        for (final de.htwdd.htwdresden.types.canteen.Meal meal : meals) {
            Log.d(LOG_TAG, meal.getTitle());
        }
    }

    /**
     * Entfernt aus dem übergebene Kalender die Zeit und gibt ein Date-Objekt zurück
     *
     * @param calendar Kalender aus dem die Zeit entfernt wird
     * @return Date-Objekt aus dem Kalender ohne Zeit
     */
    public static Date getDate(@NonNull final Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
