package de.htwdd.htwdresden.classes;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.API.ICanteenService;
import de.htwdd.htwdresden.classes.API.Retrofit2OpenMensa;
import de.htwdd.htwdresden.interfaces.IRefreshing;
import de.htwdd.htwdresden.types.canteen.Canteen;
import de.htwdd.htwdresden.types.canteen.Meal;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;

import static de.htwdd.htwdresden.classes.Const.database.Canteen.MENSA_DATE;

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
    public void updateDayMeals(@NonNull final IRefreshing iRefreshing) {
        updateDayMeals(iRefreshing, () -> {
        });
    }

    public void updateWeekMeals(@NonNull final IRefreshing iRefreshing) {
        updateWeekMeals(iRefreshing, () -> {
        });
        updateNextWeekMeals(iRefreshing, () -> {
        });
    }

    public void updateCanteens(@NonNull final IRefreshing iRefreshing) {
        updateCanteens(iRefreshing, () -> {
        });
    }

    /**
     * Erstellt eine Aufzählung aller übergebenen Speisen
     *
     * @param meals Liste von Speisen
     * @return Aufzählung von Speisen
     */
    public static String concatTitles(@NonNull final Context context, @NonNull final RealmResults<Meal> meals) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Meal meal : meals) {
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
     * Entfernt aus dem übergebenen Kalender die Zeit und gibt ein Date-Objekt zurück
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

    private static String getDateOfWeekAsString(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private static String getDateOfNextWeekAsString(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private static Date getDateOfWeek(int dayOfWeek) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + dayOfWeek);

        return calendar.getTime();
    }

    private static Date getDateOfNextWeek(int dayOfWeek) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + dayOfWeek);
        calendar.roll(Calendar.WEEK_OF_YEAR, 1);

        return calendar.getTime();
    }

    /**
     * Aktualisiert Speisen der ausgewählten Mensa
     *
     * @param iRefreshing Callback welches nach Abschluss aufgerufen wird
     * @param successFinish Callback welches nach erfolgreichen Abschluss aufgerufen wird
     */
    public void updateDayMeals(@NonNull final IRefreshing iRefreshing, @NonNull final IRefreshing successFinish) {

        final ICanteenService canteenService = Retrofit2OpenMensa.getInstance(context).getRetrofit().create(ICanteenService.class);
        final Call<List<Meal>> mealCall = canteenService.listMeals(String.valueOf(mensaId), getCurrentDateAsString());

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

    public void updateWeekMeals(@NonNull final IRefreshing iRefreshing, @NonNull final IRefreshing successFinish) {

        final ICanteenService canteenService = Retrofit2OpenMensa.getInstance(context).getRetrofit().create(ICanteenService.class);

        for ( int i = 0; i < 5; i++){
            final Call<List<Meal>> mealCall = canteenService.listMeals(String.valueOf(mensaId), getDateOfWeekAsString(getDateOfWeek(i)));

            int finalI = i;
            mealCall.enqueue(new Callback<List<Meal>>() {
                @Override
                public void onResponse(@NonNull final Call<List<Meal>> call, @NonNull final retrofit2.Response<List<Meal>> response) {
                    Log.d(LOG_TAG, "Mensa Request erfolgreich");
                    final List<Meal> meals = response.body();
                    if (meals != null) {
                        for (Meal meal : meals) {
                            meal.setDate(getDateOfWeek(finalI));
                        }
                        saveMealsOfWeek(meals);
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

//        saveMealsOfWeek(mealList);
    }

    public void updateNextWeekMeals(@NonNull final IRefreshing iRefreshing, @NonNull final IRefreshing successFinish) {

        final ICanteenService canteenService = Retrofit2OpenMensa.getInstance(context).getRetrofit().create(ICanteenService.class);

        for ( int i = 0; i < 5; i++){
            final Call<List<Meal>> mealCall = canteenService.listMeals(String.valueOf(mensaId), getDateOfNextWeekAsString(getDateOfNextWeek(i)));

            int finalI = i;
            mealCall.enqueue(new Callback<List<Meal>>() {
                @Override
                public void onResponse(@NonNull final Call<List<Meal>> call, @NonNull final retrofit2.Response<List<Meal>> response) {
                    Log.d(LOG_TAG, "Mensa Request erfolgreich");
                    final List<Meal> meals = response.body();
                    if (meals != null) {
                        for (Meal meal : meals) {
                            meal.setDate(getDateOfNextWeek(finalI));
                        }
                        saveMealsOfNextWeek(meals);
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
            meal.setDate(getDate(GregorianCalendar.getInstance()));
        }
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Meal.class).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(meals);
        realm.commitTransaction();
        realm.close();
    }

    private void saveMealsOfWeek(@NonNull final List<Meal> meals) {
        // ID der Mensa setzen
        for (final Meal meal : meals) {
            meal.setMensaId(mensaId);
        }

        if (meals.size() == 0) {
            return;
        }

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Meal.class).equalTo(Const.database.Canteen.MENSA_ID, mensaId).equalTo(MENSA_DATE, meals.get(0).getDate()).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(meals);
        realm.commitTransaction();
        realm.close();
    }

    private void saveMealsOfNextWeek(@NonNull final List<Meal> meals) {
        // ID der Mensa setzen
        for (final Meal meal : meals) {
            meal.setMensaId(mensaId);
        }

        if (meals.size() == 0) {
            return;
        }

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Meal.class).equalTo(Const.database.Canteen.MENSA_ID, mensaId).equalTo(MENSA_DATE, meals.get(0).getDate()).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(meals);
        realm.commitTransaction();
        realm.close();
    }

    public void updateCanteens(@NonNull final IRefreshing iRefreshing, @NonNull final IRefreshing successFinish) {

        final ICanteenService canteenService = Retrofit2OpenMensa.getInstance(context).getRetrofit().create(ICanteenService.class);
        final Call<List<Canteen>> canteenCall = canteenService.listCanteensOfDD();

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

        for (Canteen canteen : canteens) {
            if(canteen.getId() != 80){
                canteen.setIsFav(false);
            }
            else {
                canteen.setIsFav(true);
            }
        }

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Canteen.class).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(canteens);
        realm.commitTransaction();
        realm.close();
    }
}
