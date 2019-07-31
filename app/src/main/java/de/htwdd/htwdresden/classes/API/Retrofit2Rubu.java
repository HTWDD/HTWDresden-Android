package de.htwdd.htwdresden.classes.API;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
public class Retrofit2Rubu {
    private static Retrofit2Rubu instance = null;
    private final Retrofit retrofit;

    private Retrofit2Rubu(@NonNull final Context context) {
        final Cache cache = new Cache(context.getApplicationContext().getCacheDir(), 10 * 1024 * 1024);

        final OkHttpClient okHttpClient = getUnsafeOkHttpClient(cache);

        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(LessonUser.class, new LessonUserTypeAdapter<LessonUser>())
                .registerTypeAdapter(LessonRoom.class, new LessonUserTypeAdapter<LessonRoom>())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://rubu2.rz.htw-dresden.de/API/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit2Rubu getInstance(@NonNull final Context context) {
        if (instance == null) {
            instance = new Retrofit2Rubu(context);
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }


    private static OkHttpClient getUnsafeOkHttpClient(Cache cache) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .cache(cache)
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true).build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
