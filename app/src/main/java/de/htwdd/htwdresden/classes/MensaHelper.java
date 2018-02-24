package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.API.ICanteenService;
import de.htwdd.htwdresden.classes.API.Retrofit2Rubu;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.Realm;
import io.realm.RealmResults;
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
     * Erstellt eine Aufzählung aller übergeben Speisen
     *
     * @param meals Liste von Speisen
     * @return Aufzählung von Speisen
     */
    public static String concatTitels(@NonNull final Context context, @NonNull final RealmResults<Meal> meals) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Meal meal : meals) {
            stringBuilder.append(meal.getTitle());
            stringBuilder.append("\n\n");
        }

        if (meals.size() == 0) {
            stringBuilder.append(context.getString(R.string.mensa_no_offer));
        } else {
            final int length = stringBuilder.length();
            stringBuilder.delete(length - 2, length);
        }
        return stringBuilder.toString();
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

    /**
     * Aktualisiert Speisen der ausgewählten Mensa
     *
     * @param iRefreshing Callback welches nach Abschluss aufgerufen wird
     * @param successFinish Callback welches nach erfolgreichen Abschluss aufgerufen wird
     */
    public void updateMeals(@NonNull final IRefreshing iRefreshing, @NonNull final IRefreshing successFinish) {
        final ICanteenService canteenService = Retrofit2Rubu.getInstance(context).getRetrofit().create(ICanteenService.class);
        final Call<List<Meal>> mealCall = canteenService.listMeals(String.valueOf(mensaId));
        mealCall.enqueue(new Callback<List<Meal>>() {
            @Override
            public void onResponse(@NonNull final Call<List<Meal>> call, @NonNull final retrofit2.Response<List<Meal>> response) {
                Log.d(LOG_TAG, "Mensa Request erfolgreich");
                final List<Meal> meals = response.body();
                if (meals != null) {
                    saveMeals(meals);
                }

                // Refreshing ausschalten
                iRefreshing.onCompletion();

                successFinish.onCompletion();
            }

            @Override
            public void onFailure(@NonNull final Call<List<Meal>> call, @NonNull final Throwable t) {
                Log.e(LOG_TAG, "Fehler beim Abrufen der API", t);
                // Refreshing ausschalten
                iRefreshing.onCompletion();
            }
        });
    }

    /**
     * Speichert die Speisen in der Datenbank
     *
     * @param meals Liste von Speisen
     */
    private void saveMeals(@NonNull final List<Meal> meals) {
        // ID der Mensa setzen
        for (final Meal meal : meals) {
            meal.setMensaId(mensaId);
        }
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Meal.class).equalTo(Const.database.Canteen.MENSA_ID, mensaId).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(meals);
        realm.commitTransaction();
        realm.close();
    }
}
