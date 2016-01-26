package de.htwdd.htwdresden.classes;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * EventBus zur internen Benachrichtigung über Ereignisse
 *
 * @author Kay Förster
 */
public class EventBus {
    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance() {
        return BUS;
    }

    private EventBus() {
    }
}
