package de.htwdd.htwdresden.classes.API;

import android.content.Context;
import android.support.annotation.NonNull;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit API-Client
 *
 * @author Kay Förster
 */
public class Retrofit2Client {
    private static Retrofit2Client instance = null;
    private final Retrofit retrofit;

    private Retrofit2Client(@NonNull final Context context) {
        final Cache cache = new Cache(context.getApplicationContext().getCacheDir(), 10 * 1024 * 1024);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://rubu2.rz.htw-dresden.de/API/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit2Client getInstance(@NonNull final Context context) {
        if (instance == null) {
            instance = new Retrofit2Client(context);
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
