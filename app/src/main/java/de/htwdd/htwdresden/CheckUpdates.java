package de.htwdd.htwdresden;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.EventBus;
import de.htwdd.htwdresden.classes.MensaHelper;
import de.htwdd.htwdresden.classes.QueueCount;
import de.htwdd.htwdresden.classes.VolleyDownloader;
import de.htwdd.htwdresden.database.DatabaseManager;
import de.htwdd.htwdresden.database.SemesterPlanDAO;
import de.htwdd.htwdresden.events.UpdateAppEvent;
import de.htwdd.htwdresden.types.SemesterPlan;

/**
 *
 */
class CheckUpdates implements Runnable {
    private final static String LOG_TAG = "CheckUpdateTask";
    private final Context context;
    private final QueueCount queueCount;

    CheckUpdates(@NonNull final Context context) {
        this.context = context;
        queueCount = new QueueCount();
    }

    @Override
    public void run() {
        // Überprüfe Internetverbindung
        if (!VolleyDownloader.CheckInternet(context)) {
            return;
        }

        // Einstellungen holen
        final Calendar calendar = GregorianCalendar.getInstance();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final long mensaLastUpdate = sharedPreferences.getLong(Const.preferencesKey.PREFERENCES_MENSA_WEEK_LASTUPDATE, 0);

        // Aktualisiere Mensa
        if ((calendar.getTimeInMillis() - mensaLastUpdate) > TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
            Log.d(LOG_TAG, "Lade Mensa");
            final MensaHelper mensaHelper = new MensaHelper(context, queueCount, (short) 9);
            mensaHelper.loadAndSaveMeals(1);
            queueCount.incrementCountQueue();
            mensaHelper.loadAndSaveMeals(2);
            queueCount.incrementCountQueue();
            queueCount.update = true;
        }

        // Überprüfe Version
        checkForUpdates();

        // Wenn alle Mensa-Request abgeschlossen, Updatezeitpunkt speichern. Maximal 2 Minuten warten
        if (queueCount.update) {
            int count = 0;
            // Warte auf Mensa
            while (queueCount.getCountQueue() > 0 && count <= 240) {
                try {
                    Thread.sleep(500);
                } catch (final InterruptedException e) {
                    Log.e(LOG_TAG, "Fehler beim versuch zu schlafen", e);
                }
                count++;
            }

            // Update-Datum nur bei erfolgreichen Download speichern
            if (queueCount.getCountQueue() == 0) {
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(Const.preferencesKey.PREFERENCES_MENSA_WEEK_LASTUPDATE, calendar.getTimeInMillis());
                editor.apply();
            }
        }
    }

    /**
     * Überprüfe Modul-Informationen
     */
    private void checkForUpdates() {
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                "https://www2.htw-dresden.de/~app/API/version.json",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                            final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

                            // Überprüfe APK-Version
                            if (response.getInt("androidAPK") > packageInfo.versionCode) {
                                editor.putBoolean("appUpdate", true);
                                EventBus.getInstance().post(new UpdateAppEvent());
                            } else editor.putBoolean("appUpdate", false);

                            // Überprüfe Semesterplan
                            if (response.optLong("semesterplan_update", 0) > sharedPreferences.getLong(Const.preferencesKey.PREFERENCES_SEMESTERPLAN_UPDATETIME, -1))
                                updateSemesterplan();

                            editor.putLong("appUpdateCheck", GregorianCalendar.getInstance().getTimeInMillis());
                            editor.apply();
                        } catch (PackageManager.NameNotFoundException | JSONException e) {
                            Log.e(LOG_TAG, "[Fehler] beim Überprüfen der App-Version: Daten: " + response);
                            Log.e(LOG_TAG, e.toString());
                        }
                    }
                },
                null);
        VolleyDownloader.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Aktualisiert den Semesterplan
     */
    private void updateSemesterplan() {
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Const.internet.WEBSERVICE_URL_SEMESTERPLAN,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Datenbankzugriff
                        try {
                            // Datenbankzugriff
                            final SemesterPlanDAO semesterPlanDAO = new SemesterPlanDAO(new DatabaseManager(context));
                            // Alle Einträge löschen
                            semesterPlanDAO.clearDatabase();
                            // Einzelne Einträge durchgehen und speichern
                            for (int i = 0; i < response.length(); i++) {
                                final JSONObject semesterPlanJSON = response.getJSONObject(i);
                                final SemesterPlan semesterPlan = new SemesterPlan(semesterPlanJSON);
                                // Eintrag durchgehen speichern
                                semesterPlanDAO.save(semesterPlan);
                            }
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "[Fehler beim Parsen des Semesterplans]");
                            Log.e(LOG_TAG, e.getMessage());
                            return;
                        }

                        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                        editor.putLong(Const.preferencesKey.PREFERENCES_SEMESTERPLAN_UPDATETIME, System.currentTimeMillis());
                        editor.apply();
                    }
                },
                null);
        VolleyDownloader.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
}
