package de.htwdd.htwdresden.database;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.htwdd.htwdresden.types.Meal;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Zugriff auf die Mensa Datenbank
 *
 * @author Kay Förster
 */
public class MensaDAO {

    /**
     * Liefert die Mahlzeiten für einen Tag
     *
     * @param calendar Tag für welchen das Essen ausgeben werden soll
     * @return Liste von {@link Meal}
     */
    @Deprecated
    @NonNull
    public static RealmResults<Meal> getMealsByDate(@NonNull final Calendar calendar) {
        final Realm realm = Realm.getDefaultInstance();
        return realm.where(Meal.class).equalTo("date", MensaDAO.getDate(calendar)).findAll();
    }

    /**
     * Aktualisiert die Mahlzeiten einer Woche
     *
     * @param calendar Tag in der Woche
     * @param meals    neue Mahlzeiten
     */
    public static void updateMealsByWeek(@NonNull final Calendar calendar, @NonNull final ArrayList<Meal> meals) {
        // Ersten Tag in der Woche berechnen
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        final Date firstDay = getDate(calendar);
        // Letzten Tag in der Woche berechnen
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        final Date lastDay = calendar.getTime();
        // Datenbank Instanz holen
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        // Alte Einträge löschen
        realm.where(Meal.class).between("date", firstDay, lastDay).findAll().deleteAllFromRealm();
        // Neue Einträge hinzufügen
        realm.copyToRealmOrUpdate(meals);
        realm.commitTransaction();
    }

    /**
     * Aktualisiert die Mahlzeiten eines Tages
     *
     * @param calendar Tag welcher aktualisiert werden soll
     * @param meals    Liste der Mahlzeiten
     */
    public static void updateMealsByDay(@NonNull final Calendar calendar, @NonNull final ArrayList<Meal> meals) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        // Alte Einträge löschen
        realm.where(Meal.class).equalTo("date", getDate(calendar)).findAll().deleteAllFromRealm();
        // Neue Einträge hinzufügen
        realm.copyToRealmOrUpdate(meals);
        realm.commitTransaction();
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
