package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.support.annotation.NonNull;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.types.Meal;

/**
 * Stellt Funktionen zum Parsen der Mensa-Webseite bereit.
 *
 * @author Kay Förster
 */
public class MensaHelper {
    final private Context context;
    final private short mensaId;

    public MensaHelper(@NonNull final Context context, final short mensaId) {
        this.context = context;
        this.mensaId = mensaId;
    }

    public ArrayList<Meal> parseCurrentDay(@NonNull final String result) {
        final Calendar calendar = Calendar.getInstance();
        final ArrayList<Meal> meals = new ArrayList<>();
        final Pattern pattern = Pattern.compile(".*?<item>.*?<title>(.*?)( \\((.*?)\\))?</title>.*?details-(\\d*).html</link>.*?</item>", Pattern.DOTALL);

        final Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            final Meal meal = new Meal();

            try {
                meal.setTitle(matcher.group(1));
                meal.setPrice(matcher.group(3));
                meal.setId(Integer.parseInt(matcher.group(4)));
                meal.setImageUrl(String.format(
                        Locale.getDefault(),
                        "https://bilderspeiseplan.studentenwerk-dresden.de/m%d/%d%02d/thumbs/%d.jpg",
                        mensaId,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        meal.getId()
                ));
            } catch (Exception e) {
                meal.setTitle(context.getString(R.string.info_error_parse));
            }
            meals.add(meal);
        }
        return meals;
    }

    public ArrayList<Meal> parseCompleteWeek(@NonNull final String result) {
        final ArrayList<Meal> meals = new ArrayList<>();
        final Pattern title = Pattern.compile(".*?<td class=\"text\">(.*?)</td>.*?");
        final String[] token = result.split("class=\"speiseplan\"");
        final String[] nameOfDays = DateFormatSymbols.getInstance().getWeekdays();
        Matcher matcher;

        // Gehe Montag bis Freitag durch
        for (int i = 0; i < 5; i++) {
            final Meal meal = new Meal();
            meal.setTitle(nameOfDays[i + 2]);

            // Extrahiere die benötigten Informationen
            try {
                // Tag als Titel setzen
                matcher = title.matcher(token[i + 1]);
                // Einzelne Essen als "Preis" speichern
                while (matcher.find()) {
                    if (!meal.getPrice().isEmpty())
                        meal.setPrice(meal.getPrice() + "\n\n" + matcher.group(1));
                    else
                        meal.setPrice(matcher.group(1));
                }
                if (meal.getPrice().isEmpty())
                    meal.setPrice(context.getString(R.string.mensa_no_offer));

            } catch (Exception e) {
                meal.setTitle(context.getString(R.string.info_error_parse));
            }
            meals.add(meal);
        }

        return meals;
    }

    /**
     * Liefert eine Liste von Speisen für einen Tag aus der Wochenübersicht
     *
     * @param result HTML-Der Wochenübersicht
     * @param day    Calendertag für welchen das Essen geliefert werden soll
     * @return Liste der Essen
     */
    public ArrayList<Meal> parseDayFromWeek(@NonNull final String result, int day) {
        final ArrayList<Meal> meals = new ArrayList<>();
        final Pattern pattern = Pattern.compile(".*?<td class=\"text\">(.*?)</td>.*?>(\\d?\\d,\\d\\d|ausverkauft| )");

        // Teile Speiseplan in einzelne Tage und übergebe entsprechenden Tag an Matcher
        final String token[] = result.split("class=\"speiseplan\"");
        final Matcher matcher = pattern.matcher(token[day - 1]);

        while (matcher.find()) {
            final Meal meal = new Meal();
            meal.setTitle(matcher.group(1));
            meal.setPrice(matcher.group(2) + "€");
            meals.add(meal);
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
