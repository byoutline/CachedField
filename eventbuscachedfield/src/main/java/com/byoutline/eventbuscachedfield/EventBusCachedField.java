package com.byoutline.eventbuscachedfield;

import com.byoutline.cachedfield.CachedField;
import com.byoutline.cachedfield.CachedFieldImpl;
import com.byoutline.eventbuscachedfield.internal.EventIBus;
import com.byoutline.eventcallback.ResponseEvent;
import com.byoutline.ibuscachedfield.internal.ErrorEvent;
import com.byoutline.ibuscachedfield.internal.IBusErrorListener;
import com.byoutline.ibuscachedfield.internal.IBusSuccessListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.inject.Provider;

import de.greenrobot.event.EventBus;

import static com.byoutline.cachedfield.internal.DefaultExecutors.createDefaultStateListenerExecutor;
import static com.byoutline.cachedfield.internal.DefaultExecutors.createDefaultValueGetterExecutor;

/**
 * {@link CachedField} implementation that posts calculated result on Otto bus. <br />
 * Use {@link #builder()} to create instances.
 *
 * @param <RETURN_TYPE> Type of cached value.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class EventBusCachedField<RETURN_TYPE> extends CachedFieldImpl<RETURN_TYPE> {

    static Provider<String> defaultSessionIdProvider;
    static EventBus defaultBus;
    static ExecutorService defaultValueGetterExecutor;
    static Executor defaultStateListenerExecutor;

    EventBusCachedField(Provider<String> sessionIdProvider, Provider<RETURN_TYPE> valueGetter,
                        ResponseEvent<RETURN_TYPE> successEvent, ErrorEvent errorEvent, EventIBus bus,
                        ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        super(sessionIdProvider,
                valueGetter,
                new IBusSuccessListener<RETURN_TYPE>(bus, successEvent),
                new IBusErrorListener(bus, errorEvent),
                valueGetterExecutor,
                stateListenerExecutor);
    }

    public static <RETURN_TYPE> EventBusCachedFieldBuilder<RETURN_TYPE> builder() {
        return new EventBusCachedFieldBuilder<RETURN_TYPE>();
    }

    public static void init(Provider<String> defaultSessionIdProvider, EventBus defaultBus) {
        init(defaultSessionIdProvider, defaultBus,
                createDefaultValueGetterExecutor(),
                createDefaultStateListenerExecutor());
    }

    public static void init(Provider<String> defaultSessionIdProvider, EventBus defaultBus,
                            ExecutorService defaultValueGetterExecutor, Executor defaultStateListenerExecutor) {
        EventBusCachedField.defaultSessionIdProvider = defaultSessionIdProvider;
        EventBusCachedField.defaultBus = defaultBus;
        EventBusCachedField.defaultValueGetterExecutor = defaultValueGetterExecutor;
        EventBusCachedField.defaultStateListenerExecutor = defaultStateListenerExecutor;
    }
}
