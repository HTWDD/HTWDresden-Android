package de.htwdd.htwdresden.classes.internet;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import de.htwdd.htwdresden.classes.Const;

/**
 * asynchroner HTTP-Download über Projekt Volley
 *
 * @author Kay Förster
 */
public class VolleyDownloader {
    private static VolleyDownloader ourInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleyDownloader(@NonNull final Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized VolleyDownloader getInstance(@NonNull final Context context) {
        if (ourInstance == null)
            ourInstance = new VolleyDownloader(context.getApplicationContext());
        return ourInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        if (mImageLoader == null)
            mImageLoader = new ImageLoader(getRequestQueue(), new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

                @Override
                public Bitmap getBitmap(@NonNull final String url) {
                    return mCache.get(url);
                }

                @Override
                public void putBitmap(@NonNull final String url, @NonNull final Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }
            });
        return mImageLoader;
    }

    /**
     * Fügt einen Request zur Abarbeitungswarteschlange hinzu
     *
     * @param request Request welcher hinzugefügt werden soll
     * @param <T>     Typ des Requests
     */
    public <T> void addToRequestQueue(@NonNull final Request<T> request) {
        mRequestQueue.add(request);
    }

    /**
     * Überprüft ob aktuell eine Internetverbindung besteht.
     *
     * @param context Referenz auf die aktuelle Activity
     * @return true=Internet verbindung vorhanden, sonst false
     */
    public static boolean CheckInternet(@NonNull final Context context) {
        final ConnectivityManager systemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Network[] networks = systemService.getAllNetworks();

        for (final Network network : networks) {
            if (systemService.getNetworkInfo(network).isConnected())
                return true;
        }
        return false;
    }

    public static int getResponseCode(@NonNull final VolleyError error) {
        if (error.networkResponse != null)
            return error.networkResponse.statusCode;
        else
            // Wenn kein Response vom Server kommt, genauere Fehlermeldung unterscheiden
            if (error instanceof TimeoutError)
                return Const.internet.HTTP_TIMEOUT;
            else if (error instanceof NoConnectionError)
                return Const.internet.HTTP_NO_CONNECTION;
            else if (error instanceof NetworkError)
                return Const.internet.HTTP_NETWORK_ERROR;
            else return Const.internet.HTTP_DOWNLOAD_ERROR;
    }
}
