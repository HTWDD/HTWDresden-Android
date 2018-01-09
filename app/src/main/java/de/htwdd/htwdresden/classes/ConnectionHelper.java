package de.htwdd.htwdresden.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.support.annotation.NonNull;
import android.util.Log;


public class ConnectionHelper {

    /**
     * Überprüft ob aktuell eine Internetverbindung besteht.
     *
     * @param context Referenz auf die aktuelle App-Context
     * @return true=Internet verbindung vorhanden, sonst false
     */
    public static boolean checkInternetConnection(@NonNull final Context context) {
        final ConnectivityManager systemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (systemService == null) {
            Log.d("ConnectionHelper", "ConnectionManager nicht verfügbar");
            return false;
        }

        final Network[] networks = systemService.getAllNetworks();
        for (final Network network : networks) {
            if (systemService.getNetworkInfo(network).isConnected())
                return true;
        }
        return false;
    }
}
