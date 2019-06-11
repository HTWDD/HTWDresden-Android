package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import android.util.Log;


public class ConnectionHelper {

    /**
     * Überprüft ob aktuell eine Internetverbindung besteht.
     *
     * @param context Referenz auf die aktuelle App-Context
     * @return true=keine Internet verbindung vorhanden, sonst false
     */
    public static boolean checkNoInternetConnection(@NonNull final Context context) {
        final ConnectivityManager systemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (systemService == null) {
            Log.d("ConnectionHelper", "ConnectionManager nicht verfügbar");
            return true;
        }

        final NetworkInfo activeNetworkInfo = systemService.getActiveNetworkInfo();
        return activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting();
    }
}
