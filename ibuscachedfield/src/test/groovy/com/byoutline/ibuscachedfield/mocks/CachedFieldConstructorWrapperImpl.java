package com.byoutline.ibuscachedfield.mocks;

import com.byoutline.cachedfield.CachedField;
import com.byoutline.cachedfield.CachedFieldImpl;
import com.byoutline.eventcallback.IBus;
import com.byoutline.eventcallback.ResponseEvent;
import com.byoutline.ibuscachedfield.builders.CachedFieldConstructorWrapper;
import com.byoutline.ibuscachedfield.internal.ErrorEvent;
import com.byoutline.ibuscachedfield.internal.IBusErrorListener;
import com.byoutline.ibuscachedfield.internal.IBusSuccessListener;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedFieldConstructorWrapperImpl implements CachedFieldConstructorWrapper<String, IBus, CachedField<String>> {
    @Override
    public CachedField<String> build(Provider<String> sessionIdProvider, Provider<String> valueGetter,
                                     ResponseEvent<String> successEvent, ErrorEvent errorEvent, IBus iBus,
                                     ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        return new CachedFieldImpl<String>(sessionIdProvider, valueGetter,
                new IBusSuccessListener<String>(iBus, successEvent),
                new IBusErrorListener(iBus, errorEvent),
                valueGetterExecutor,
                stateListenerExecutor);
    }
}
