package com.byoutline.eventbuscachedfield;

import com.byoutline.cachedfield.CachedField;
import com.byoutline.cachedfield.CachedFieldWithArgImpl;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.eventbuscachedfield.internal.EventIBus;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;
import com.byoutline.ibuscachedfield.internal.IBusErrorListenerWithArg;
import com.byoutline.ibuscachedfield.internal.IBusSuccessListenerWithArg;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.inject.Provider;

/**
 * {@link CachedField} implementation that posts calculated result on Otto bus.<br />
 * Use {@link #builder()} to create instances.
 *
 * @param <RETURN_TYPE> Type of cached value.
 * @param <ARG_TYPE>    Type of argument that needs to be passed to calculate value.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class EventBusCachedFieldWithArg<RETURN_TYPE, ARG_TYPE> extends CachedFieldWithArgImpl<RETURN_TYPE, ARG_TYPE> {

    EventBusCachedFieldWithArg(Provider<String> sessionIdProvider,
                               ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                               ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> successEvent,
                               ResponseEventWithArg<Exception, ARG_TYPE> errorEvent, EventIBus bus,
                               ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        super(sessionIdProvider,
                valueGetter,
                new IBusSuccessListenerWithArg<RETURN_TYPE, ARG_TYPE>(bus, successEvent),
                new IBusErrorListenerWithArg<ARG_TYPE>(bus, errorEvent),
                valueGetterExecutor, stateListenerExecutor);
    }

    public static <RETURN_TYPE, ARG_TYPE> EventBusCachedFieldWithArgBuilder<RETURN_TYPE, ARG_TYPE> builder() {
        return new EventBusCachedFieldWithArgBuilder<RETURN_TYPE, ARG_TYPE>();
    }
}
