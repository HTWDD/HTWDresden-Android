package de.htwdd.htwdresden.classes.API;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.htwdd.htwdresden.adapter.typeadapter.LessonUserTypeAdapter;
import de.htwdd.htwdresden.types.LessonRoom;
import de.htwdd.htwdresden.types.LessonUser;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit API-Client für rubu2.rz.htw-dresden.de
 *
 * @author Kay Förster
 */
public class Retrofit2OpenMensa {
    private static Retrofit2OpenMensa instance = null;
    private final Retrofit retrofit;

    private Retrofit2OpenMensa(@NonNull final Context context) {
        final Cache cache = new Cache(context.getApplicationContext().getCacheDir(), 10 * 1024 * 1024);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(LessonUser.class, new LessonUserTypeAdapter<LessonUser>())
                .registerTypeAdapter(LessonRoom.class, new LessonUserTypeAdapter<LessonRoom>())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://openmensa.org/api/v2/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit2OpenMensa getInstance(@NonNull final Context context) {
        if (instance == null) {
            instance = new Retrofit2OpenMensa(context);
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
