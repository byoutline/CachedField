package com.byoutline.eventbuscachedfield

import com.byoutline.cachedfield.CachedField
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.cachedfield.MockFactory
import com.byoutline.eventcallback.ResponseEvent
import de.greenrobot.event.EventBus
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Provider

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class EventBusCachedFieldSpec extends Specification {
    @Shared
    String value = "value"
    @Shared
    Exception exception = new RuntimeException("Cached Field test exception")
    ResponseEvent<String> successEvent
    ResponseEvent<Exception> errorEvent
    EventBus bus

    static void postAndWaitUntilFieldStopsLoading(CachedField field) {
        boolean duringValueLoad = true
        def listener = { FieldState newState ->
            if (newState == FieldState.NOT_LOADED || newState == FieldState.LOADED) {
                duringValueLoad = false
            }
        } as FieldStateListener

        field.addStateListener(listener)
        field.postValue()
        while (duringValueLoad) {
            sleep 1
        }
        field.removeStateListener(listener)
        // allow thread switch from state listener executor to success listener
        sleep 8
    }

    def setup() {
        bus = Mock()
        successEvent = Mock()
        errorEvent = Mock()

        EventBusCachedField.init(MockFactory.getSameSessionIdProvider(), bus)
    }

    def "postValue should return immediately"() {
        given:
        EventBusCachedField field = EventBusCachedField.builder()
                .withValueProvider(MockFactory.getDelayedStringGetter(value, 1000))
                .withSuccessEvent(successEvent)
                .build()

        when:
        boolean tookToLong = false
        Thread.start {
            sleep 40
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
        EventBusCachedField field = EventBusCachedField.builder()
                .withValueProvider(valProv)
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build()
        postAndWaitUntilFieldStopsLoading(field)

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
        EventBusCachedField field = EventBusCachedField.builder()
                .withValueProvider(MockFactory.getFailingStringGetter(exception))
                .withSuccessEvent(successEvent)
                .withGenericErrorEvent(expEvent)
                .build()

        when:
        postAndWaitUntilFieldStopsLoading(field)

        then:
        1 * bus.post(expEvent)
    }

    def "when custom bus passed to builder it should be used instead of default"() {
        given:
        def sessionProv = { return "custom" } as Provider<String>
        EventBus customBus = Mock()
        EventBusCachedField field = EventBusCachedField.builder()
                .withValueProvider(MockFactory.getStringGetter("val"))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .withCustomSessionIdProvider(sessionProv)
                .withCustomBus(customBus)
                .build()

        when:
        postAndWaitUntilFieldStopsLoading(field)

        then:
        1 * customBus.post(_)
        0 * bus.post(_)
    }
}
