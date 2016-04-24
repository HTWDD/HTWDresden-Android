package de.htwdd.htwdresden.classes;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * EventBus zur internen Benachrichtigung über Ereignisse
 *
 * @author Kay Förster
 */
public class EventBus extends Bus {
    private final Handler mainThread = new Handler(Looper.getMainLooper());
    private static final EventBus BUS = new EventBus();

    private EventBus() {
        super(ThreadEnforcer.ANY);
    }

    public static EventBus getInstance() {
        return BUS;
    }

    /**
     * Alle Events auf den Main-Thread weiterleiten
     *
     * @param event Event
     */
    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }

}
