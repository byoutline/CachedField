package com.byoutline.observablecachedfield;

import com.byoutline.cachedfield.ErrorListenerWithArg;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.SuccessListenerWithArg;
import com.byoutline.eventcallback.IBus;
import com.byoutline.ibuscachedfield.IBusCachedFieldWithArgBuilder;
import com.byoutline.ibuscachedfield.builders.CachedFieldWithArgConstructorWrapper;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArgImpl;
import com.byoutline.ibuscachedfield.internal.*;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Fluent interface builder of {@link ObservableCachedFieldWithArg}.
 *
 * @param <RETURN_TYPE> Type of object to be cached.
 * @param <ARG_TYPE>    Type of argument that needs to be passed to calculate value.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class ObservableCachedFieldWithArgBuilder<RETURN_TYPE, ARG_TYPE, BUS>
        extends IBusCachedFieldWithArgBuilder<RETURN_TYPE, ARG_TYPE, BUS, ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>> {
    protected ObservableCachedFieldWithArgBuilder(Provider<String> sessionIdProvider,
                                                  BUS bus, BusConverter<BUS> busConverter,
                                                  ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        super(new ConstructorWrapper<RETURN_TYPE, ARG_TYPE, BUS>(busConverter),
                bus,
                sessionIdProvider,
                valueGetterExecutor,
                stateListenerExecutor);
    }

    protected interface BusConverter<BUS> {
        IBus convert(BUS bus);
    }

    private static class ConstructorWrapper<RETURN_TYPE, ARG_TYPE, BUS> implements CachedFieldWithArgConstructorWrapper<RETURN_TYPE, ARG_TYPE, BUS, ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>> {
        private final BusConverter<BUS> busConverter;

        private ConstructorWrapper(BusConverter<BUS> busConverter) {
            this.busConverter = busConverter;
        }

        @Override
        public ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE> build(Provider<String> sessionIdProvider,
                                                                         ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                                                                         ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> successEvent,
                                                                         ResponseEventWithArg<Exception, ARG_TYPE> errorEvent,
                                                                         BUS bus,
                                                                         ExecutorService valueGetterExecutor,
                                                                         Executor stateListenerExecutor) {
            if (stateListenerExecutor == null || valueGetter == null) {
                throw new NullArgumentException();
            }
            // If user did setup events use listeners that posts them,
            // otherwise use stub listeners that do nothing.
            boolean noEvents = successEvent instanceof NoEvents;
            final SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> additionalSuccessListener;
            final ErrorListenerWithArg<ARG_TYPE> additionalErrorListener;
            if (noEvents) {
                additionalSuccessListener = new StubSuccessListenerWithArg<RETURN_TYPE, ARG_TYPE>();
                additionalErrorListener = new StubErrorListenerWithArg<ARG_TYPE>();
            } else {
                IBus iBus = busConverter.convert(bus);
                additionalSuccessListener = new IBusSuccessListenerWithArg<RETURN_TYPE, ARG_TYPE>(iBus, successEvent);
                additionalErrorListener = new IBusErrorListenerWithArg<ARG_TYPE>(iBus, errorEvent);
            }

            return new ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>(
                    sessionIdProvider, valueGetter,
                    additionalSuccessListener,
                    additionalErrorListener,
                    valueGetterExecutor, stateListenerExecutor);
        }
    }

    private static class NoEvents<RETURN_TYPE, ARG_TYPE> extends ResponseEventWithArgImpl<RETURN_TYPE, ARG_TYPE> {
    }

    @Override
    public EventChoice withValueProvider(ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueProvider) {
        return new EventChoice(super.withValueProvider(valueProvider));
    }

    public class EventChoice extends SuccessEvent {
        private final SuccessEvent supperSuccessEventSetter;

        public EventChoice(SuccessEvent supperSuccessEventSetter) {
            this.supperSuccessEventSetter = supperSuccessEventSetter;
        }

        @Override
        public ErrorEventSetter withSuccessEvent(ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> successEvent) {
            return supperSuccessEventSetter.withSuccessEvent(successEvent);
        }

        public OverrideDefaultsSetter withoutEvents() {
            NoEvents<RETURN_TYPE, ARG_TYPE> event = new NoEvents<RETURN_TYPE, ARG_TYPE>();
            return supperSuccessEventSetter.withSuccessEvent(event).withResponseErrorEvent(null);
        }
    }
}
