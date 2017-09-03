package com.byoutline.observablecachedfield

import android.databinding.Observable
import android.databinding.ObservableField
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.byoutline.eventcallback.IBus
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg
import com.byoutline.ibuscachedfield.events.ResponseEventWithArgImpl
import com.byoutline.ibuscachedfield.internal.NullArgumentException
import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class ObservableCachedFieldWithArgSpec extends Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']

    @Unroll
    def "should notify about new value: #val for arg: #arg"() {
        given:
        ObservableCachedFieldWithArg field = builder()
                .withValueProvider(MockFactory.getStringGetter(argToValueMap))
                .withoutEvents()
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .build()
        def callback = new MockObservableCallback()
        ObservableField<String> obs = field.observable()
        obs.addOnPropertyChangedCallback(callback)

        when:
        field.postValue(arg)

        then:
        callback.called
        obs.get() == val

        where:
        val | arg
        'a' | 1
        'b' | 2
    }


    def "should post success value on ibus"() {
        given:
        ResponseEventWithArg<String, Integer> successEvent = Mock()
        def field = builder()
                .withValueProvider(MockFactory.getStringGetter(argToValueMap))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(new ResponseEventWithArgImpl<Exception, Integer>())
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .build()
        when:
        field.postValue(1)
        then:
        1 * successEvent.setResponse('a', 1)
    }

    def "should post error value on ibus"() {
        given:
        ResponseEventWithArg<Exception, Integer> errorEvent = Mock()
        def field = builder()
                .withValueProvider(MockFactory.getFailingStringGetterWithArg())
                .withSuccessEvent(new ResponseEventWithArgImpl<String, Integer>())
                .withResponseErrorEvent(errorEvent)
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .build()
        when:
        field.postValue(8)
        then:
        1 * errorEvent.setResponse(_, 8)
    }

    def "should set error value in observable"() {
        given:
        ObservableCachedFieldWithArg field = builder()
                .withValueProvider(MockFactory.getFailingStringGetterWithArg())
                .withoutEvents()
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .build()
        def callback = new MockObservableCallback()
        ObservableField<String> errObs = field.getObservableError()
        errObs.addOnPropertyChangedCallback(callback)
        when:
        field.postValue(8)
        then:
        callback.called
        errObs.get() instanceof RuntimeException
        errObs.get().message == "E8"
    }

    def "builder should not allow null value getter"() {
        when:
        builder()
                .withValueProvider(null)
                .withoutEvents()
                .build()
        then:
        thrown NullArgumentException
    }

    def "builder should not allow null state listener executor"() {
        when:
        builder()
                .withValueProvider(MockFactory.getStringGetter(argToValueMap))
                .withoutEvents()
                .withCustomStateListenerExecutor(null)
                .build()
        then:
        thrown NullArgumentException
    }

    private ObservableCachedFieldWithArgBuilder<String, Integer, IBus> builder() {
        IBus bus = Mock()
        def busConverter = { b -> b } as ObservableCachedFieldWithArgBuilder.BusConverter<IBus>
        return new ObservableCachedFieldWithArgBuilder<String, Integer, IBus>(MockFactory.getSameSessionIdProvider(), bus, busConverter,
                DefaultExecutors.createDefaultValueGetterExecutor(), DefaultExecutors.createDefaultStateListenerExecutor())
    }
}

class MockObservableCallback extends Observable.OnPropertyChangedCallback {
    public boolean called

    @Override
    void onPropertyChanged(Observable sender, int propertyId) {
        called = true
    }
}