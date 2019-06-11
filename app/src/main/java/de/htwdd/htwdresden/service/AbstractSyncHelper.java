package de.htwdd.htwdresden.service;

import android.app.IntentService;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import de.htwdd.htwdresden.R;
import de.htwdd.htwdresden.classes.QueueCount;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Basisfunktionalitäten für Sync-Prozesse bereitstellen
 *
 * @author Kay Förster
 */
public abstract class AbstractSyncHelper extends IntentService {
    private final static String TAG = "AbstractSyncHelper";
    final QueueCount queueCount;
    final Context context = this;
    private boolean cancel = false;
    @Nullable
    protected BroadcastNotifier broadcastNotifier;

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

    /**
     * Blockiert bis alle Aufgaben erledigt sind und {@link QueueCount#countQueue} gleich 0 ist
     */
    void waitForFinish() {
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
    void setError(@NonNull final String errorMessage, final int errorCode) {
        // Synchronisation abbrechen
        cancel = true;
        // Benachrichtigung senden
        if (broadcastNotifier != null) {
            broadcastNotifier.notifyStatus(errorCode, errorMessage);
        }
    }

    abstract class GenericCallback<T> implements Callback<T> {
        @Override
        public void onResponse(@NonNull final Call<T> call, @NonNull final Response<T> response) {
            if (response.isSuccessful()) {
                onSuccess(response.body());
            } else {
                final int responseCode = response.code();
                Log.d(TAG, "Fehler beim Ausführen des Requests. Code: "+  response);
                String message;
                switch (responseCode) {
                    case 401:
                        message = getString(R.string.exams_result_wrong_auth);
                        break;
                    case 404:
                        message = getString(R.string.info_internet_no_connection);
                        break;
                    default:
                        message = getString(R.string.info_internet_error);
                        break;
                }
                setError(message, responseCode);
            }
        }

        @Override
        public void onFailure(@NonNull final Call<T> call, @NonNull final Throwable t) {
            setError("Sync Error", 999);
            Log.d(TAG, "Fehler beim Ausführen des Requests ", t);
        }

        abstract void onSuccess(@Nullable final T response);
    }
}
