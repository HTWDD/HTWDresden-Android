package de.htwdd.htwdresden.classes.API;

import android.content.Context;

import androidx.annotation.NonNull;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit API-Client für die QIS-Schnittstelle
 *
 * @author Kay Förster
 */
public class Retrofit2Qis {
    private static Retrofit2Qis instance = null;
    private final Retrofit retrofit;

    private Retrofit2Qis(@NonNull final Context context) {
        final Cache cache = new Cache(context.getApplicationContext().getCacheDir(), 10 * 1024 * 1024);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://wwwqis.htw-dresden.de/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit2Qis getInstance(@NonNull final Context context) {
        if (instance == null) {
            instance = new Retrofit2Qis(context);
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
