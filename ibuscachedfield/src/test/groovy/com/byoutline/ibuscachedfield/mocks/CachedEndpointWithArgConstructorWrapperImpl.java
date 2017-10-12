package com.byoutline.ibuscachedfield.mocks;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArgImpl;
import com.byoutline.cachedfield.cachedendpoint.StateAndValue;
import com.byoutline.eventcallback.IBus;
import com.byoutline.ibuscachedfield.builders.CachedEndpointWithArgConstructorWrapper;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;
import com.byoutline.ibuscachedfield.internal.BusCallEndListener;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedEndpointWithArgConstructorWrapperImpl implements CachedEndpointWithArgConstructorWrapper<String, Integer, IBus, CachedEndpointWithArg<String, Integer>> {

    @Override
    public CachedEndpointWithArg<String, Integer> build(Provider<String> sessionIdProvider, ProviderWithArg<String, Integer> valueGetter,
                                                        ResponseEventWithArg<StateAndValue<String, Integer>, Integer> resultEvent, IBus iBus,
                                                        ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        return new CachedEndpointWithArgImpl<String, Integer>(sessionIdProvider, valueGetter,
                new BusCallEndListener<String, Integer>(iBus, resultEvent),
                valueGetterExecutor,
                stateListenerExecutor);
    }
}
