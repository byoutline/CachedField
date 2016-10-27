package com.byoutline.ibuscachedfield.mocks;

import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.cachedfield.CachedFieldWithArgImpl;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.eventcallback.IBus;
import com.byoutline.ibuscachedfield.builders.CachedFieldWithArgConstructorWrapper;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;
import com.byoutline.ibuscachedfield.internal.IBusErrorListenerWithArg;
import com.byoutline.ibuscachedfield.internal.IBusSuccessListenerWithArg;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedFieldWithArgConstructorWrapperImpl implements CachedFieldWithArgConstructorWrapper<String, Integer, IBus, CachedFieldWithArg<String, Integer>> {

    @Override
    public CachedFieldWithArg<String, Integer> build(Provider<String> sessionIdProvider, ProviderWithArg<String, Integer> valueGetter,
                                                     ResponseEventWithArg<String, Integer> successEvent, ResponseEventWithArg<Exception, Integer> errorEvent, IBus iBus,
                                                     ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        return new CachedFieldWithArgImpl<String, Integer>(sessionIdProvider, valueGetter,
                new IBusSuccessListenerWithArg<String, Integer>(iBus, successEvent),
                new IBusErrorListenerWithArg<Integer>(iBus, errorEvent),
                valueGetterExecutor,
                stateListenerExecutor);
    }
}
