package com.byoutline.ibuscachedfield;

import com.byoutline.cachedfield.CachedField;
import com.byoutline.eventcallback.ResponseEvent;
import com.byoutline.ibuscachedfield.builders.CachedFieldConstructorWrapper;
import com.byoutline.ibuscachedfield.internal.ErrorEvent;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Fluent interface builder of {@link CachedField}.
 *
 * @param <RETURN_TYPE>  Type of object to be cached.
 * @param <BUS>          Type of bus that will be used to post events.
 * @param <CACHED_FIELD> Specific type of {@link CachedField} returned.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public abstract class IBusCachedFieldBuilder<RETURN_TYPE, BUS, CACHED_FIELD extends CachedField<RETURN_TYPE>> {

    private final CachedFieldConstructorWrapper<RETURN_TYPE, BUS, CACHED_FIELD> constructorWrapper;
    private Provider<RETURN_TYPE> valueGetter;
    private ResponseEvent<RETURN_TYPE> successEvent;
    private ErrorEvent errorEvent;
    private Provider<String> sessionIdProvider;
    private BUS bus;
    private ExecutorService valueGetterExecutor;
    private Executor stateListenerExecutor;

    protected IBusCachedFieldBuilder(CachedFieldConstructorWrapper<RETURN_TYPE, BUS, CACHED_FIELD> constructorWrapper,
                                     BUS defaultBus,
                                     Provider<String> defaultSessionIdProvider,
                                     ExecutorService defaultValueGetterExecutor,
                                     Executor defaultStateListenerExecutor) {
        this.constructorWrapper = constructorWrapper;
        bus = defaultBus;
        sessionIdProvider = defaultSessionIdProvider;
        valueGetterExecutor = defaultValueGetterExecutor;
        stateListenerExecutor = defaultStateListenerExecutor;
    }

    public SuccessEvent withValueProvider(Provider<RETURN_TYPE> valueProvider) {
        this.valueGetter = valueProvider;
        return new SuccessEvent();
    }

    public class SuccessEvent {

        protected SuccessEvent() {
        }

        public ErrorEventSetter withSuccessEvent(ResponseEvent<RETURN_TYPE> successEvent) {
            IBusCachedFieldBuilder.this.successEvent = successEvent;
            return new ErrorEventSetter();
        }
    }

    public class ErrorEventSetter {

        protected ErrorEventSetter() {
        }

        public OverrideDefaultsSetter withGenericErrorEvent(Object errorEvent) {
            IBusCachedFieldBuilder.this.errorEvent = ErrorEvent.genericEvent(errorEvent);
            return new OverrideDefaultsSetter();
        }

        public OverrideDefaultsSetter withResponseErrorEvent(ResponseEvent<Exception> errorEvent) {
            IBusCachedFieldBuilder.this.errorEvent = ErrorEvent.responseEvent(errorEvent);
            return new OverrideDefaultsSetter();
        }

        public CachedField<RETURN_TYPE> build() {
            IBusCachedFieldBuilder.this.errorEvent = new ErrorEvent(null, null);
            return IBusCachedFieldBuilder.this.build();
        }
    }

    public class OverrideDefaultsSetter {

        protected OverrideDefaultsSetter() {
        }

        public OverrideDefaultsSetter withCustomSessionIdProvider(Provider<String> sessionIdProvider) {
            IBusCachedFieldBuilder.this.sessionIdProvider = sessionIdProvider;
            return this;
        }

        public OverrideDefaultsSetter withCustomBus(BUS bus) {
            IBusCachedFieldBuilder.this.bus = bus;
            return this;
        }

        public OverrideDefaultsSetter withCustomValueGetterExecutor(ExecutorService valueGetterExecutor) {
            IBusCachedFieldBuilder.this.valueGetterExecutor = valueGetterExecutor;
            return this;
        }

        public OverrideDefaultsSetter withCustomStateListenerExecutor(Executor stateListenerExecutor) {
            IBusCachedFieldBuilder.this.stateListenerExecutor = stateListenerExecutor;
            return this;
        }

        public CachedField<RETURN_TYPE> build() {
            return IBusCachedFieldBuilder.this.build();
        }
    }

    protected CachedField<RETURN_TYPE> build() {
        return constructorWrapper.build(sessionIdProvider, valueGetter, successEvent, errorEvent, bus,
                valueGetterExecutor, stateListenerExecutor);
    }
}
