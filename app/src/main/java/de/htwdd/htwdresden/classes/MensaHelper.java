package de.htwdd.htwdresden.classes;

import android.content.Context;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.htwdd.htwdresden.R;

/**
 * Stellt Funktionen zum Parsen der Mensa-Webseite bereit.
 *
 * @author Kay Förster
 */
public class MensaHelper {
    private Context context;
    private short mensaId;

    public MensaHelper(Context context, short mensaId) {
        this.context = context;
        this.mensaId = mensaId;
    }

    public ArrayList<Meal> parseCurrentDay(String result) {
        Calendar calendar = Calendar.getInstance();
        ArrayList<Meal> meals = new ArrayList<>();
        Pattern pattern = Pattern.compile(".*?<item>.*?<title>(.*?)( \\((.*?)\\))?</title>.*?details-(\\d*).html</link>.*?</item>", Pattern.DOTALL);

        Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            Meal meal = new Meal();

            try {
                meal.setTitle(matcher.group(1));
                meal.setPrice(matcher.group(3));
                meal.setId(Integer.parseInt(matcher.group(4)));
                meal.setImageUrl("https://bilderspeiseplan.studentenwerk-dresden.de/m" + mensaId + "/" + calendar.get(Calendar.YEAR) + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "/thumbs/" + meal.getId() + ".jpg");
            } catch (Exception e) {
                meal.setTitle(context.getString(R.string.info_error_parse));
            }
            meals.add(meal);
        }
        return meals;
    }

    public ArrayList<Meal> parseCompleteWeek(String result) {
        ArrayList<Meal> meals = new ArrayList<>();
        Pattern title = Pattern.compile(".*?<td class=\"text\">(.*?)</td>.*?");
        String[] token = result.split("class=\"speiseplan\"");
        String[] nameOfDays = DateFormatSymbols.getInstance().getWeekdays();
        Matcher matcher;

        // Gehe Montag bis Freitag durch
        for (int i = 0; i < 5; i++) {
            Meal meal = new Meal();
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
     * Liefert die Mensa URL zum jeweiligen Modus. Aktuell wird nur die Mensa Reichenbachstraße Unterstützt
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
