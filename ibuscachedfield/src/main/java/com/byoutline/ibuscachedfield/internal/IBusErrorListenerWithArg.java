package com.byoutline.ibuscachedfield.internal;

import com.byoutline.cachedfield.ErrorListenerWithArg;
import com.byoutline.eventcallback.IBus;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class IBusErrorListenerWithArg<ARG_TYPE> implements ErrorListenerWithArg<ARG_TYPE> {

    private final IBus bus;
    private final ResponseEventWithArg<Exception, ARG_TYPE> event;

    public IBusErrorListenerWithArg(@Nonnull IBus bus, @Nullable ResponseEventWithArg<Exception, ARG_TYPE> event) {
        this.bus = bus;
        this.event = event;
    }

    @Override
    public void valueLoadingFailed(Exception ex, ARG_TYPE arg) {
        if (event != null) {
            event.setResponse(ex, arg);
            bus.post(event);
        }
    }
}
