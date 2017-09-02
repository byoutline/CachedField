package com.byoutline.eventbuscachedfield;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg;
import com.byoutline.cachedfield.cachedendpoint.StateAndValue;
import com.byoutline.eventbuscachedfield.internal.EventIBus;
import com.byoutline.ibuscachedfield.IBusCachedEndpointWithArgBuilder;
import com.byoutline.ibuscachedfield.builders.CachedEndpointWithArgConstructorWrapper;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.inject.Provider;

import de.greenrobot.event.EventBus;

/**
 * Fluent interface for building instances of {@link EventBusCachedEndpointWithArg}.
 */
public class EventBusCachedEndpointWithArgBuilder<RETURN_TYPE, ARG_TYPE>
        extends IBusCachedEndpointWithArgBuilder<RETURN_TYPE, ARG_TYPE, EventBus, CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE>> {

    protected EventBusCachedEndpointWithArgBuilder() {
        super(new ConstructorWrapper<RETURN_TYPE, ARG_TYPE>(),
                EventBusCachedField.defaultBus,
                EventBusCachedField.defaultSessionIdProvider,
                EventBusCachedField.defaultValueGetterExecutor,
                EventBusCachedField.defaultStateListenerExecutor);
    }

    private static class ConstructorWrapper<RETURN_TYPE, ARG_TYPE>
            implements CachedEndpointWithArgConstructorWrapper<RETURN_TYPE, ARG_TYPE, EventBus, CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE>> {
        @Override
        public CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE> build(Provider<String> sessionIdProvider,
                                                                  ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                                                                  ResponseEventWithArg<StateAndValue<RETURN_TYPE, ARG_TYPE>, ARG_TYPE> resultEvent,
                                                                  EventBus bus,
                                                                  ExecutorService valueGetterExecutor,
                                                                  Executor stateListenerExecutor) {
            return new EventBusCachedEndpointWithArg<RETURN_TYPE, ARG_TYPE>(sessionIdProvider, valueGetter,
                    resultEvent, new EventIBus(bus),
                    valueGetterExecutor, stateListenerExecutor);
        }
    }
}
