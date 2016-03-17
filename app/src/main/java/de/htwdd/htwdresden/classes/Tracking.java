package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
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
    private static Context mcontext;

    private Tracking(Context context) {
        mcontext = context;
    }

    public static void makeRequest(Context context) {
        if (tracking == null) {
            tracking = new Tracking(context);
            tracking.makeRequest(0);
        } else tracking.makeRequest(1);
    }

    private void makeRequest(int type) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("plattform", 0);
            jsonObject.put("api", Build.VERSION.RELEASE);
            jsonObject.put("version", mcontext.getPackageManager().getPackageInfo(mcontext.getPackageName(), 0).versionName);
            jsonObject.put("unique", Settings.Secure.getString(mcontext.getContentResolver(), Settings.Secure.ANDROID_ID));
            jsonObject.put("type", type);

            // Sende Request an Webservice
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://track.benchr.de/track", jsonObject, null, null);
            VolleyDownloader.getInstance(mcontext).getRequestQueue().add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e(LOG_TAG, "[Fehler] Beim Tracking-Aufruf");
            Log.e(LOG_TAG, e.toString());
        }
    }
}
