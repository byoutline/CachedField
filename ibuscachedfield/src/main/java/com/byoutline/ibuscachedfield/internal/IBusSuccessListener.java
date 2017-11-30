package com.byoutline.ibuscachedfield.internal;

import com.byoutline.cachedfield.SuccessListener;
import com.byoutline.eventcallback.IBus;
import com.byoutline.eventcallback.ResponseEvent;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public final class IBusSuccessListener<RETURN_TYPE> implements SuccessListener<RETURN_TYPE> {

    private final IBus bus;
    private final ResponseEvent<RETURN_TYPE> responseEvent;

    public IBusSuccessListener(IBus bus, ResponseEvent<RETURN_TYPE> responseEvent) {
        this.bus = bus;
        this.responseEvent = responseEvent;
    }

    @Override
    public void valueLoaded(RETURN_TYPE t) {
        responseEvent.setResponse(t);
        bus.post(responseEvent);
    }
}
