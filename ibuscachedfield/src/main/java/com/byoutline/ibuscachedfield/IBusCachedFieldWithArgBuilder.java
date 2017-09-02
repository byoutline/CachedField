package com.byoutline.ibuscachedfield;

import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.ibuscachedfield.builders.CachedFieldWithArgConstructorWrapper;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.inject.Provider;

/**
 * Fluent interface builder of {@link CachedFieldWithArg}.
 *
 * @param <RETURN_TYPE>  Type of object to be cached.
 * @param <ARG_TYPE>     Type of argument that needs to be passed to calculate value.
 * @param <BUS>          Type of bus that will be used to post events.
 * @param <CACHED_FIELD> Specific type of {@link CachedFieldWithArg} returned.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public abstract class IBusCachedFieldWithArgBuilder
        <RETURN_TYPE, ARG_TYPE, BUS, CACHED_FIELD extends CachedFieldWithArg<RETURN_TYPE, ARG_TYPE>> {

    private final CachedFieldWithArgConstructorWrapper<RETURN_TYPE, ARG_TYPE, BUS, CACHED_FIELD> constructorWrapper;
    private ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter;
    private ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> successEvent;
    private ResponseEventWithArg<Exception, ARG_TYPE> errorEvent;
    private Provider<String> sessionIdProvider;
    private BUS bus;
    private ExecutorService valueGetterExecutor;
    private Executor stateListenerExecutor;

    protected IBusCachedFieldWithArgBuilder(CachedFieldWithArgConstructorWrapper<RETURN_TYPE, ARG_TYPE, BUS, CACHED_FIELD> constructorWrapper,
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

    public SuccessEvent withValueProvider(ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueProvider) {
        this.valueGetter = valueProvider;
        return new SuccessEvent();
    }

    public class SuccessEvent {

        protected SuccessEvent() {
        }

        public ErrorEventSetter withSuccessEvent(ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> successEvent) {
            IBusCachedFieldWithArgBuilder.this.successEvent = successEvent;
            return new ErrorEventSetter();
        }
    }

    public class ErrorEventSetter {

        protected ErrorEventSetter() {
        }

        public OverrideDefaultsSetter withResponseErrorEvent(@Nullable ResponseEventWithArg<Exception, ARG_TYPE> errorEvent) {
            IBusCachedFieldWithArgBuilder.this.errorEvent = errorEvent;
            return new OverrideDefaultsSetter();
        }

        public CachedFieldWithArg<RETURN_TYPE, ARG_TYPE> build() {
            return IBusCachedFieldWithArgBuilder.this.build();
        }
    }

    public class OverrideDefaultsSetter {

        protected OverrideDefaultsSetter() {
        }

        public OverrideDefaultsSetter withCustomSessionIdProvider(Provider<String> sessionIdProvider) {
            IBusCachedFieldWithArgBuilder.this.sessionIdProvider = sessionIdProvider;
            return this;
        }

        public OverrideDefaultsSetter withCustomBus(BUS bus) {
            IBusCachedFieldWithArgBuilder.this.bus = bus;
            return this;
        }

        public OverrideDefaultsSetter withCustomValueGetterExecutor(ExecutorService valueGetterExecutor) {
            IBusCachedFieldWithArgBuilder.this.valueGetterExecutor = valueGetterExecutor;
            return this;
        }

        public OverrideDefaultsSetter withCustomStateListenerExecutor(Executor stateListenerExecutor) {
            IBusCachedFieldWithArgBuilder.this.stateListenerExecutor = stateListenerExecutor;
            return this;
        }

        public CACHED_FIELD build() {
            return IBusCachedFieldWithArgBuilder.this.build();
        }
    }

    protected CACHED_FIELD build() {
        return constructorWrapper.build(sessionIdProvider, valueGetter, successEvent, errorEvent, bus,
                valueGetterExecutor, stateListenerExecutor);
    }
}
