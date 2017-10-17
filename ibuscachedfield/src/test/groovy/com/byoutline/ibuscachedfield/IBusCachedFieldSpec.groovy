package com.byoutline.ibuscachedfield

import com.byoutline.cachedfield.CachedField
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.cachedfield.MockCachedFieldLoader
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.testsuite.StateListenerSuiteSpec
import com.byoutline.eventcallback.IBus
import com.byoutline.eventcallback.ResponseEvent
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Provider

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class IBusCachedFieldSpec extends StateListenerSuiteSpec {
    @Shared
    String value = "value"
    @Shared
    Exception exception = new RuntimeException("Cached Field test exception")
    ResponseEvent<String> successEvent
    ResponseEvent<Exception> errorEvent
    IBus bus

    def setup() {
        bus = Mock()
        successEvent = Mock()
        errorEvent = Mock()
    }

    def "postValue should return immediately"() {
        given:
        CachedField field = IBusMockFactory.fieldWithoutArgBuilder(bus)
                .withValueProvider(MockFactory.getDelayedStringGetter(value, 1000))
                .withSuccessEvent(successEvent)
                .build()

        when:
        boolean tookToLong = false
        Thread.start {
            sleep 15
            tookToLong = true
        }
        field.postValue()

        then:
        if (tookToLong) {
            throw new AssertionError("Test took to long to execute")
        }
    }

    @Unroll
    def "should post success times: #sC, error times: #eC for valueProvider: #valProv"() {
        when:
        CachedField field = IBusMockFactory.fieldWithoutArgBuilder(bus)
                .withValueProvider(valProv)
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build()
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
        CachedField field = IBusMockFactory.fieldWithoutArgBuilder(bus)
                .withValueProvider(MockFactory.getFailingStringGetter(exception))
                .withSuccessEvent(successEvent)
                .withGenericErrorEvent(expEvent)
                .build()

        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field)

        then:
        1 * bus.post(expEvent)
    }

    def "when custom bus passed to builder it should be used instead of default"() {
        given:
        def sessionProv = { return "custom" } as Provider<String>
        IBus customBus = Mock()
        CachedField field = IBusMockFactory.fieldWithoutArgBuilder(bus)
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
    def getField() {
        def sessionProv = { return "custom" } as Provider<String>
        CachedField field = IBusMockFactory.fieldWithoutArgBuilder(bus)
                .withValueProvider(MockFactory.getStringGetter("val"))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .withCustomSessionIdProvider(sessionProv)
                .build()
        return field
    }


    @Override
    def waitUntilFieldFinishAction(Object field) {
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field)
    }
}
