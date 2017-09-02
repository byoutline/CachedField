package com.byoutline.ibuscachedfield;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArgImpl;
import com.byoutline.cachedfield.cachedendpoint.StateAndValue;
import com.byoutline.ibuscachedfield.builders.CachedEndpointWithArgConstructorWrapper;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.inject.Provider;

/**
 * Fluent interface for building {@link CachedEndpointWithArgImpl}
 *
 * @param <RETURN_TYPE>     Type of object to be cached.
 * @param <ARG_TYPE>        Type of argument that needs to be passed to make an API call.
 * @param <BUS>             Type of bus that will be used to post events.
 * @param <CACHED_ENDPOINT> Specific type of {@link CachedEndpointWithArg} returned.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public abstract class IBusCachedEndpointWithArgBuilder<RETURN_TYPE, ARG_TYPE, BUS, CACHED_ENDPOINT extends CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE>> {

    private final CachedEndpointWithArgConstructorWrapper<RETURN_TYPE, ARG_TYPE, BUS, CACHED_ENDPOINT> constructorWrapper;
    private ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter;
    private ResponseEventWithArg<StateAndValue<RETURN_TYPE, ARG_TYPE>, ARG_TYPE> resultEvent;
    private Provider<String> sessionIdProvider;
    private BUS bus;
    private ExecutorService valueGetterExecutor;
    private Executor stateListenerExecutor;

    protected IBusCachedEndpointWithArgBuilder(CachedEndpointWithArgConstructorWrapper<RETURN_TYPE, ARG_TYPE, BUS, CACHED_ENDPOINT> constructorWrapper,
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

    public ResultEvent withValueProvider(ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueProvider) {
        this.valueGetter = valueProvider;
        return new ResultEvent();
    }

    public class ResultEvent {

        protected ResultEvent() {
        }

        public OverrideDefaultsSetter withResultEvent(
                ResponseEventWithArg<StateAndValue<RETURN_TYPE, ARG_TYPE>, ARG_TYPE> resultEvent) {
            IBusCachedEndpointWithArgBuilder.this.resultEvent = resultEvent;
            return new OverrideDefaultsSetter();
        }
    }

    public class OverrideDefaultsSetter {

        protected OverrideDefaultsSetter() {
        }

        public OverrideDefaultsSetter withCustomSessionIdProvider(Provider<String> sessionIdProvider) {
            IBusCachedEndpointWithArgBuilder.this.sessionIdProvider = sessionIdProvider;
            return this;
        }

        public OverrideDefaultsSetter withCustomBus(BUS bus) {
            IBusCachedEndpointWithArgBuilder.this.bus = bus;
            return this;
        }

        public OverrideDefaultsSetter withCustomValueGetterExecutor(ExecutorService valueGetterExecutor) {
            IBusCachedEndpointWithArgBuilder.this.valueGetterExecutor = valueGetterExecutor;
            return this;
        }

        public OverrideDefaultsSetter withCustomStateListenerExecutor(Executor stateListenerExecutor) {
            IBusCachedEndpointWithArgBuilder.this.stateListenerExecutor = stateListenerExecutor;
            return this;
        }

        public CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE> build() {
            return IBusCachedEndpointWithArgBuilder.this.build();
        }
    }

    protected CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE> build() {
        return constructorWrapper.<RETURN_TYPE, ARG_TYPE>build(sessionIdProvider, valueGetter,
                resultEvent, bus,
                valueGetterExecutor, stateListenerExecutor);
    }
}
