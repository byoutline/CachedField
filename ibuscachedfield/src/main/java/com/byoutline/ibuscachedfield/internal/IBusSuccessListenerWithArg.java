package com.byoutline.ibuscachedfield.internal;

import com.byoutline.cachedfield.SuccessListenerWithArg;
import com.byoutline.eventcallback.IBus;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public final class IBusSuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> implements SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> {

    private final IBus bus;
    private final ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> responseEvent;

    public IBusSuccessListenerWithArg(IBus bus, ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> responseEvent) {
        this.bus = bus;
        this.responseEvent = responseEvent;
    }

    @Override
    public void valueLoaded(RETURN_TYPE value, ARG_TYPE arg) {
        responseEvent.setResponse(value, arg);
        bus.post(responseEvent);
    }

}
