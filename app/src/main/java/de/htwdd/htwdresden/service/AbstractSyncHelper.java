package de.htwdd.htwdresden.service;

import android.app.IntentService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import de.htwdd.htwdresden.classes.QueueCount;

/**
 * Basisfunktionalitäten für Sync-Prozesse bereitstellen
 *
 * @author Kay Förster
 */
public abstract class AbstractSyncHelper extends IntentService {
    final QueueCount queueCount;
    final Context context = this;
    final BroadcastNotifier broadcastNotifier;
    private boolean cancel = false;

    public AbstractSyncHelper(@NonNull final String name, @NonNull final String intentCategory) {
        super(name);
        queueCount = new QueueCount();
        broadcastNotifier = new BroadcastNotifier(this, intentCategory);
    }

    boolean isCancel() {
        return cancel;
    }

    void setCancelToTrue() {
        cancel = true;
    }

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
}
