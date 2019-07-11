package de.htwdd.htwdresden.classes;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.API.ICanteenService;
import de.htwdd.htwdresden.classes.API.ICanteenService2;
import de.htwdd.htwdresden.classes.API.Retrofit2OpenMensa;
import de.htwdd.htwdresden.classes.API.Retrofit2Rubu;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.canteen.Canteen;
import de.htwdd.htwdresden.types.canteen.Meal;
import de.htwdd.htwdresden.types.canteen.Meal2;
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

    public void updateCanteens(@NonNull final IRefreshing iRefreshing) {
        updateMeals(iRefreshing, () -> {
        });
    }

    /**
     * Erstellt eine Aufzählung aller übergeben Speisen
     *
     * @param meals Liste von Speisen
     * @return Aufzählung von Speisen
     */
    public static String concatTitels(@NonNull final Context context, @NonNull final RealmResults<Meal2> meals) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Meal2 meal : meals) {
            stringBuilder.append(meal.getName());
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

    private static String getCurrentDateAsString() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * Aktualisiert Speisen der ausgewählten Mensa
     *
     * @param iRefreshing Callback welches nach Abschluss aufgerufen wird
     * @param successFinish Callback welches nach erfolgreichen Abschluss aufgerufen wird
     */
    public void updateMeals(@NonNull final IRefreshing iRefreshing, @NonNull final IRefreshing successFinish) {

        final ICanteenService canteenService = Retrofit2Rubu.getInstance(context).getRetrofit().create(ICanteenService.class);
        final Call<List<Meal>> mealCall = canteenService.listMeals("1");

        final ICanteenService2 canteenService2 = Retrofit2OpenMensa.getInstance(context).getRetrofit().create(ICanteenService2.class);
        final Call<List<Meal2>> meal2Call = canteenService2.listMeals(String.valueOf(mensaId), getCurrentDateAsString());

        meal2Call.enqueue(new Callback<List<Meal2>>() {
            @Override
            public void onResponse(@NonNull final Call<List<Meal2>> call, @NonNull final retrofit2.Response<List<Meal2>> response) {
                Log.d(LOG_TAG, "Mensa Request erfolgreich");
                final List<Meal2> meals = response.body();
                if (meals != null) {
                    saveMeals2(meals);
                }

                // Refreshing ausschalten
                iRefreshing.onCompletion();

                successFinish.onCompletion();
            }

            @Override
            public void onFailure(@NonNull final Call<List<Meal2>> call, @NonNull final Throwable t) {
                Log.e(LOG_TAG, "Fehler beim Abrufen der API", t);
                // Refreshing ausschalten
                iRefreshing.onCompletion();
            }
        });

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
            meal.setMensaId(Short.valueOf("1"));
        }
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Meal.class).equalTo(Const.database.Canteen.MENSA_ID, 1).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(meals);
        realm.commitTransaction();
        realm.close();
    }

    private void saveMeals2(@NonNull final List<Meal2> meals) {
        // ID der Mensa setzen
        for (final Meal2 meal : meals) {
            meal.setMensaId(mensaId);
            meal.setDate(getDate(GregorianCalendar.getInstance()));
        }
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Meal2.class).equalTo(Const.database.Canteen.MENSA_ID, mensaId).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(meals);
        realm.commitTransaction();
        realm.close();
    }

    public void updateCanteens(@NonNull final IRefreshing iRefreshing, @NonNull final IRefreshing successFinish) {

        final ICanteenService2 canteenService2 = Retrofit2OpenMensa.getInstance(context).getRetrofit().create(ICanteenService2.class);
        final Call<List<Canteen>> canteenCall = canteenService2.listCanteens();

        canteenCall.enqueue(new Callback<List<Canteen>>() {
            @Override
            public void onResponse(@NonNull final Call<List<Canteen>> call, @NonNull final retrofit2.Response<List<Canteen>> response) {
                Log.d(LOG_TAG, "Mensa Request erfolgreich");
                final List<Canteen> canteens = response.body();
                if (canteens != null) {
                    saveCanteens(canteens);
                }

                // Refreshing ausschalten
                iRefreshing.onCompletion();

                successFinish.onCompletion();
            }

            @Override
            public void onFailure(@NonNull final Call<List<Canteen>> call, @NonNull final Throwable t) {
                Log.e(LOG_TAG, "Fehler beim Abrufen der API", t);
                // Refreshing ausschalten
                iRefreshing.onCompletion();
            }
        });
    }

    private void saveCanteens(final List<Canteen> canteens) {

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(canteens);
        realm.commitTransaction();
        realm.close();
    }
}
