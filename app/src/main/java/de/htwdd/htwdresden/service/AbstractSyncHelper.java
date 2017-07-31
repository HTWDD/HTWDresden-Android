package de.htwdd.htwdresden.service;

import android.app.IntentService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.Const;
import de.htwdd.htwdresden.classes.QueueCount;
import de.htwdd.htwdresden.classes.internet.VolleyDownloader;

/**
 * Basisfunktionalitäten für Sync-Prozesse bereitstellen
 *
 * @author Kay Förster
 */
public abstract class AbstractSyncHelper extends IntentService {
    final QueueCount queueCount;
    final Context context = this;
    private boolean cancel = false;
    @Nullable
    protected BroadcastNotifier broadcastNotifier;
    /**
     * Standard Error Listener für die Bestimmung der Fehlerursache, ruft anschließen {@link #setError(String, int)}
     */
    protected final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(final VolleyError error) {
            // Bestimme Fehlermeldung
            final String message;
            switch (VolleyDownloader.getResponseCode(error)) {
                case Const.internet.HTTP_TIMEOUT:
                    message = getString(R.string.info_internet_timeout);
                    break;
                case Const.internet.HTTP_NO_CONNECTION:
                case Const.internet.HTTP_NOT_FOUND:
                    message = getString(R.string.info_internet_no_connection);
                    break;
                case Const.internet.HTTP_UNAUTHORIZED:
                    message = getString(R.string.exams_result_wrong_auth);
                    break;
                case Const.internet.HTTP_NETWORK_ERROR:
                default:
                    message = getString(R.string.info_internet_error);
            }
            setError(message, VolleyDownloader.getResponseCode(error));
            queueCount.decrementCountQueue();
            Log.e("AbstractSyncHelper", "[Fehler] Konnte Ressource nicht abrufen!", error);
        }
    };

    public AbstractSyncHelper(@NonNull final String name, @NonNull final String intentCategory) {
        super(name);
        queueCount = new QueueCount();
        try {
            broadcastNotifier = new BroadcastNotifier(context, intentCategory);
        } catch (final Exception e) {
            Log.d("AbstractSync", "Es steht kein BroadcastNotifier zur Verfügung");
            broadcastNotifier = null;
        }
    }

    boolean isCancel() {
        return cancel;
    }

    void setCancelToTrue() {
        cancel = true;
    }

    /**
     * Blockiert bis alle Aufgaben erledigt sind und {@link QueueCount#countQueue} gleich 0 ist
     */
    protected void waitForFinish() {
        while (queueCount.getCountQueue() > 0 && !cancel) {
            try {
                Log.d("AbstractSyncHelper", "Gehe schlafen, Warteschlange: " + queueCount.getCountQueue());
                Thread.sleep(250, 0);
            } catch (final InterruptedException e) {
                Log.e("AbstractSyncHelper", "[ERROR] Fehler beim Schlafen", e);
            }
        }
    }

    /**
     * Behandelt alle Maßnahmen wenn ein Fehler aufgetreten ist
     *
     * @param errorMessage Fehlerbeschreibung welche an den User weitergeleitet werden kann
     * @param errorCode Fehlercode zur genaueren Differenzierung
     */
    abstract void setError(@NonNull final String errorMessage, final int errorCode);
}
