package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.Meal;
import io.realm.Realm;

/**
 * Stellt Funktionen zum Parsen der Mensa-Webseite bereit.
 *
 * @author Kay Förster
 */
public class MensaHelper {
    final private static String LOG_TAG = "MensaHelper";
    final private Context context;
    final private short mensaId;
    @Nullable
    private QueueCount queueCount = null;
    final private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(LOG_TAG, "[Fehler] Beim Laden der Mensa", error);
        }
    };

    public MensaHelper(@NonNull final Context context, final short mensaId) {
        this.context = context;
        this.mensaId = mensaId;
    }

    public MensaHelper(@NonNull final Context context, @NonNull final QueueCount queueCount, final short mensaId) {
        this(context, mensaId);
        this.queueCount = queueCount;
    }

    /**
     * Aktualisiert den Speiseplan
     *
     * @param modus 0: aktuelles Angebot, 1: Angebot der aktuellen Woche, 2: Angebot der nächsten Woche
     */
    public void loadAndSaveMeals(final int modus) {
        final Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = new String(response.getBytes(Charset.forName("iso-8859-1")), Charset.forName("UTF-8"));
                final Calendar calendar = GregorianCalendar.getInstance();
                final ArrayList<Meal> meals;
                switch (modus) {
                    case 1:
                        // Parse Ergebnis
                        meals = parseCompleteWeek(response, GregorianCalendar.getInstance());
                        // Speichern
                        updateMealsByWeek(calendar, meals);
                        break;
                    case 2:
                        calendar.add(Calendar.WEEK_OF_YEAR, 1);
                        // Parse Ergebnis
                        meals = parseCompleteWeek(response, calendar);
                        // Speichern
                        updateMealsByWeek(calendar, meals);
                        break;
                    default:
                        // Parse und speichere Ergebnis
                        updateMealsByDay(GregorianCalendar.getInstance(), parseCurrentDay(response));
                        break;
                }

                if (queueCount != null)
                    queueCount.decrementCountQueue();
            }
        };
        // Download der Informationen
        final StringRequest stringRequest = new StringRequest(getMensaUrl(modus), stringListener, errorListener);
        VolleyDownloader.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * Extrahiert den Speiseplan des aktuellen Tages aus der RSS-Übersicht
     *
     * @param result RSS-Struktur
     * @return Liste von Speisen
     */
    public ArrayList<Meal> parseCurrentDay(@NonNull final String result) {
        final Calendar calendar = GregorianCalendar.getInstance();
        final ArrayList<Meal> meals = new ArrayList<>();
        final Pattern pattern = Pattern.compile(".*?<item>.*?<title>(.*?)( \\((\\d.\\d\\d|ausverkauft).*?\\))</title>.*?details-(\\d*).html</link>.*?</item>", Pattern.DOTALL);

        final Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            final Meal meal = new Meal();

            try {
                meal.setMensaId(mensaId);
                meal.setTitle(matcher.group(1));
                meal.setPrice(matcher.group(3));
                meal.setId(Integer.parseInt(matcher.group(4)));
                meal.setDate(calendar);
            } catch (Exception e) {
                meal.setTitle(context.getString(R.string.info_error_parse));
            }
            meals.add(meal);
        }
        return meals;
    }

    /**
     * Extrahiert den Speiseplan einer Woche aus der HTML-Übersicht
     *
     * @param result   HTML-Seite des Speiseplans
     * @param calendar Tag mit welchem die Woche beginnt
     * @return Liste aller Speisen in der Woche
     */
    @NonNull
    private ArrayList<Meal> parseCompleteWeek(@NonNull final String result, @NonNull final Calendar calendar) {
        final ArrayList<Meal> meals = new ArrayList<>();
        final Pattern title = Pattern.compile("<td class=\"text\">(.*?)</td>.*?details-(\\d*).*?>(\\d?\\d,\\d\\d|ausverkauft| )");
        final String[] tokens = result.split("class=\"speiseplan\"");
        Matcher matcher;

        // Gehe Montag bis Freitag durch
        int day = 2;
        for (final String token : tokens) {
            // Überspringe mögliche Aktionen in der Woche oder HTML-Body
            if (token.contains("id=\"aktionen\"") || token.startsWith("<!DOCTYPE html PUBLIC")) {
                continue;
            }

            // Extrahiere die benötigten Informationen
            try {
                matcher = title.matcher(token);
                calendar.set(Calendar.DAY_OF_WEEK, day);

                while (matcher.find()) {
                    final Meal meal = new Meal();
                    meal.setId(Integer.parseInt(matcher.group(2)));
                    meal.setTitle(matcher.group(1));
                    meal.setMensaId(mensaId);
                    meal.setDate(calendar);
                    meal.setPrice(matcher.group(3));
                    meals.add(meal);
                }
            } catch (final Exception e) {
                Log.e(LOG_TAG, "Fehler beim Extrahieren der Gerichte für Tag: " + day, e);
                // Hinweis für User einfügen
                final Meal meal = new Meal();
                meal.setTitle(context.getString(R.string.info_error_parse));
                meals.add(meal);
            } finally {
                day++;
            }
        }

        return meals;
    }

    /**
     * Liefert die Mensa URL zum jeweiligen Modus. Aktuell wird nur die Mensa Reichenbachstraße unterstützt
     *
     * @param modus 0: aktuelles Angebot, 1: Angebot der aktuellen Woche, 2: Angebot der nächsten Woche
     * @return URL des Speiseplans
     */
    @NonNull
    public static String getMensaUrl(final int modus) {
        switch (modus) {
            // Angebot aktuelle Woche
            case 1:
                return "https://www.studentenwerk-dresden.de/mensen/speiseplan/mensa-reichenbachstrasse.html?print=1";
            // Angebot nächste Woche
            case 2:
                return "https://www.studentenwerk-dresden.de/mensen/speiseplan/mensa-reichenbachstrasse-w1.html?print=1";
            // Angebot heute
            default:
                return "https://www.studentenwerk-dresden.de/feeds/speiseplan.rss?mid=" + 9;
        }
    }

    /**
     * Aktualisiert die Mahlzeiten einer Woche
     *
     * @param calendar Tag in der Woche
     * @param meals    neue Mahlzeiten
     */
    private static void updateMealsByWeek(@NonNull final Calendar calendar, @NonNull final ArrayList<Meal> meals) {
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
