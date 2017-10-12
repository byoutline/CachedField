package com.byoutline.eventbuscachedfield.internal;

import com.byoutline.eventcallback.IBus;
import de.greenrobot.event.EventBus;

/**
 * Wraps given instance of {@link EventBus} so it implements {@link IBus}.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class EventIBus implements IBus {

    private final EventBus bus;

    public EventIBus(EventBus bus) {
        this.bus = bus;
    }

    @Override
    public void post(Object event) {
        bus.post(event);
    }

    public void register(Object object) {
        bus.register(object);
    }
}
