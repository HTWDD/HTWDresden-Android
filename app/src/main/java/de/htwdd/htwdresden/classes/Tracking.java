package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import android.util.Log;

import de.htwdd.htwdresden.classes.API.IGeneralService;
import de.htwdd.htwdresden.classes.API.Retrofit2Rubu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Sendet anonymisierte Daten zur Nutzung der App an die Entwickler
 *
 * @author Kay Förster
 */
public class Tracking {
    private static TrackingData data;

    private Tracking() {
    }

    public static void makeRequest(@NonNull final Context context) {
        if (data == null) {
            data = new TrackingData();
            data.unique = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            try {
                data.version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (final PackageManager.NameNotFoundException ignored) {
            }
        } else {
            data.type = 1;
        }

        Retrofit2Rubu.getInstance(context).getRetrofit().create(IGeneralService.class).tracking(data).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull final Call<Void> call, @NonNull final Response<Void> response) {
            }

            @Override
            public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {
                Log.e("Tracking", "[Fehler] Beim Tracking-Aufruf", t);
            }
        });
    }

    public static class TrackingData {
        private final int plattform = 0;
        private final String api = Build.VERSION.RELEASE;
        public String version;
        public int type;
        String unique;
    }
}
