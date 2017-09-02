package com.byoutline.eventbuscachedfield;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArgImpl;
import com.byoutline.cachedfield.cachedendpoint.StateAndValue;
import com.byoutline.eventcallback.IBus;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;
import com.byoutline.ibuscachedfield.internal.BusCallEndListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nonnull;
import javax.inject.Provider;

/**
 * {@link EventBusCachedField} like API for making non GET calls.
 *
 * @param <RETURN_TYPE> Type of value returned by endpoint on success.
 * @param <ARG_TYPE>    Type of argument that needs to be passed to make a call.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 * @see <a href="https://github.com/byoutline/CachedField#cachedendpoint">https://github.com/byoutline/CachedField#cachedendpoint</a>
 */
public class EventBusCachedEndpointWithArg<RETURN_TYPE, ARG_TYPE> extends CachedEndpointWithArgImpl<RETURN_TYPE, ARG_TYPE> {

    EventBusCachedEndpointWithArg(@Nonnull Provider<String> sessionProvider,
                                  @Nonnull ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                                  @Nonnull ResponseEventWithArg<StateAndValue<RETURN_TYPE, ARG_TYPE>, ARG_TYPE> event,
                                  @Nonnull IBus bus,
                                  @Nonnull ExecutorService valueGetterExecutor,
                                  @Nonnull Executor stateListenerExecutor) {
        super(sessionProvider, valueGetter,
                new BusCallEndListener<RETURN_TYPE, ARG_TYPE>(bus, event),
                valueGetterExecutor, stateListenerExecutor);
    }

    public static <RETURN_TYPE, ARG_TYPE> EventBusCachedEndpointWithArgBuilder<RETURN_TYPE, ARG_TYPE> builder() {
        return new EventBusCachedEndpointWithArgBuilder<RETURN_TYPE, ARG_TYPE>();
    }
}