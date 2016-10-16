package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.database.MensaDAO;
import de.htwdd.htwdresden.events.UpdateMensaEvent;
import de.htwdd.htwdresden.types.Meal;

/**
 * Stellt Funktionen zum Parsen der Mensa-Webseite bereit.
 *
 * @author Kay Förster
 */
public class MensaHelper {
    final private static String LOG_TAG = "MensaHelper";
    final private Context context;
    final private short mensaId;
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

    /**
     * Aktualisiert den Speisplan
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
                        MensaDAO.updateMealsByWeek(calendar, meals);
                        break;
                    case 2:
                        calendar.add(Calendar.WEEK_OF_YEAR, 1);
                        // Parse Ergebnis
                        meals = parseCompleteWeek(response, calendar);
                        // Speichern
                        MensaDAO.updateMealsByWeek(calendar, meals);
                        break;
                    default:
                        // Parse und speichere Ergebnis
                        MensaDAO.updateMealsByDay(GregorianCalendar.getInstance(), parseCurrentDay(response));
                        break;
                }

                EventBus.getInstance().post(new UpdateMensaEvent(modus));
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
    private ArrayList<Meal> parseCompleteWeek(@NonNull final String result, @NonNull final Calendar calendar) {
        final ArrayList<Meal> meals = new ArrayList<>();
        final Pattern title = Pattern.compile("<td class=\"text\">(.*?)</td>.*?details-(\\d*).*?>(\\d?\\d,\\d\\d|ausverkauft| )");
        final String[] token = result.split("class=\"speiseplan\"");
        Matcher matcher;

        // Gehe Montag bis Freitag durch
        for (int i = 0; i < 5; i++) {
            // Extrahiere die benötigten Informationen
            try {
                matcher = title.matcher(token[i + 1]);
                calendar.set(Calendar.DAY_OF_WEEK, i + 2);

                while (matcher.find()) {
                    final Meal meal = new Meal();
                    meal.setId(Integer.parseInt(matcher.group(2)));
                    meal.setTitle(matcher.group(1));
                    meal.setMensaId(mensaId);
                    meal.setDate(calendar);
                    meal.setPrice(matcher.group(3));
                    meals.add(meal);
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Fehler beim Matchen der Mensa");
                Log.e(LOG_TAG, e.getMessage());
                // Hinweis für User einfügen
                final Meal meal = new Meal();
                meal.setTitle(context.getString(R.string.info_error_parse));
                meals.add(meal);
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
}
