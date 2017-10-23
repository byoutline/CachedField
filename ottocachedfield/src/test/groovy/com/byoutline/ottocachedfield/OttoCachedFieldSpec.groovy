package com.byoutline.ottocachedfield

import com.byoutline.cachedfield.CachedField
import com.byoutline.cachedfield.MockCachedFieldLoader
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.testsuite.CachedFieldCommonSuiteSpec
import com.byoutline.eventcallback.ResponseEvent
import com.google.common.util.concurrent.MoreExecutors
import com.squareup.otto.Bus
import spock.lang.Shared
import spock.lang.Unroll

import javax.inject.Provider
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class OttoCachedFieldSpec extends CachedFieldCommonSuiteSpec {
    @Shared
    Exception exception = new RuntimeException("Cached Field test exception")
    ResponseEvent<String> successEvent
    ResponseEvent<Exception> errorEvent
    Bus bus

    def setup() {
        bus = Mock()
        successEvent = Mock()
        errorEvent = Mock()

        OttoCachedField.init(MockFactory.getSameSessionIdProvider(), bus)
    }


    @Unroll
    def "should post success times: #sC, error times: #eC for valueProvider: #valProv"() {
        given:
        CachedField field = getFieldWithDefaultExecutors(valProv)
        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field)

        then:
        sC * successEvent.setResponse(value)
        eC * errorEvent.setResponse(exception)

        where:
        sC | eC | valProv
        0  | 1  | MockFactory.getFailingStringGetter(exception)
        1  | 0  | MockFactory.getStringGetter(value)
    }

    def "postValue should post generic error"() {
        given:
        Object expEvent = "exp"
        CachedField field = OttoCachedField.builder()
                .withValueProvider(MockFactory.getFailingStringGetter(exception))
                .withSuccessEvent(successEvent)
                .withGenericErrorEvent(expEvent)
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .build()

        when:
        field.postValue()

        then:
        1 * bus.post(expEvent)
    }

    def "2 arg constructor should post on default bus"() {
        given:
        def field = new OttoCachedField(MockFactory.getStringGetter(value), successEvent)

        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field)

        then:
        1 * bus.post(_)
    }

    def "3 arg constructor with error ResponseEvent should post on default bus"() {
        given:
        def field = new OttoCachedField(MockFactory.getStringGetter(value), successEvent, errorEvent)

        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field)

        then:
        1 * bus.post(_)
    }

    def "3 arg constructor with generic error event should post on default bus"() {
        given:
        def field = new OttoCachedField(MockFactory.getStringGetter(value), successEvent, new Object())

        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field)

        then:
        1 * bus.post(_)
    }

    def "when custom bus passed to builder it should be used instead of default"() {
        given:
        def sessionProv = { return "custom" } as Provider<String>
        Bus customBus = Mock()
        CachedField field = OttoCachedField.builder()
                .withValueProvider(MockFactory.getStringGetter("val"))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .withCustomSessionIdProvider(sessionProv)
                .withCustomBus(customBus)
                .build()

        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field)

        then:
        1 * customBus.post(_)
        0 * bus.post(_)
    }

    @Override
    def getField(Provider<String> valueProvider, ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        return OttoCachedField.builder()
                .withValueProvider(valueProvider)
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .withCustomValueGetterExecutor(valueGetterExecutor)
                .withCustomStateListenerExecutor(stateListenerExecutor)
                .build()
    }

}
