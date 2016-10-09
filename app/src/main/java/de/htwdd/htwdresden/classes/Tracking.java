package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Sendet anonymisierte Daten zur Nutzung der App an die Entwickler
 *
 * @author Kay Förster
 */
public class Tracking {
    private static final String LOG_TAG = "Tracking";
    private static Tracking tracking;

    private Tracking() {
    }

    public static void makeRequest(@NonNull final Context context) {
        if (tracking == null) {
            tracking = new Tracking();
            tracking.makeRequest(context, 0);
        } else tracking.makeRequest(context, 1);
    }

    private void makeRequest(@NonNull final Context context, final int type) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("plattform", 0);
            jsonObject.put("api", Build.VERSION.RELEASE);
            jsonObject.put("version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
            jsonObject.put("unique", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            jsonObject.put("type", type);

            // Sende Request an Webservice
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://track.benchr.de/track", jsonObject, null, null);
            VolleyDownloader.getInstance(context).getRequestQueue().add(jsonObjectRequest);
        } catch (final Exception e) {
            Log.e(LOG_TAG, "[Fehler] Beim Tracking-Aufruf", e);
        }
    }
}
