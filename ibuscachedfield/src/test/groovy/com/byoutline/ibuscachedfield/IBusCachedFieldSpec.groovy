package com.byoutline.ibuscachedfield

import com.byoutline.cachedfield.CachedField
import com.byoutline.cachedfield.MockCachedFieldLoader
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.testsuite.CachedFieldCommonSuiteSpec
import com.byoutline.eventcallback.IBus
import com.byoutline.eventcallback.ResponseEvent
import spock.lang.Shared
import spock.lang.Unroll

import javax.inject.Provider
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class IBusCachedFieldSpec extends CachedFieldCommonSuiteSpec {
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
    def getField(Provider<String> valueProvider, ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        return IBusMockFactory.fieldWithoutArgBuilder(bus)
                .withValueProvider(valueProvider)
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .withCustomValueGetterExecutor(valueGetterExecutor)
                .withCustomStateListenerExecutor(stateListenerExecutor)
                .build()
    }
}
