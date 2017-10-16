package com.byoutline.eventbuscachedfield

import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.cachedfield.MockCachedFieldLoader
import com.byoutline.cachedfield.MockFactory
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg
import de.greenrobot.event.EventBus
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Provider

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class EventBusCachedFieldWithArgSpec extends Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']
    ResponseEventWithArg<String, Integer> successEvent
    ResponseEventWithArg<Exception, Integer> errorEvent
    EventBus bus

    def setup() {
        bus = Mock()
        successEvent = Mock()
        errorEvent = Mock()

        EventBusCachedField.init(MockFactory.getSameSessionIdProvider(), bus)
    }

    @Unroll
    def "should post value: #val , times: #sC for arg: #arg"() {
        given:
        EventBusCachedFieldWithArg field = EventBusCachedFieldWithArg.builder()
                .withValueProvider(MockFactory.getStringGetter(argToValueMap))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build()
        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field, arg)

        then:

        sC * successEvent.setResponse(val, arg)
        0 * errorEvent.setResponse(_, _)

        where:
        val  | arg | sC
        null | 0   | 1
        'a'  | 1   | 1
        'b'  | 2   | 1
    }

    def "postValue should post error with argument"() {
        given:
        Exception errorVal = null
        Integer errorArg = null
        ResponseEventWithArg<Exception, Integer> errorEvent =
                { Exception val, Integer arg ->
                    errorVal = val; errorArg = arg
                } as ResponseEventWithArg<Exception, Integer>
        EventBusCachedFieldWithArg field = EventBusCachedFieldWithArg.builder()
                .withValueProvider(MockFactory.getFailingStringGetterWithArg())
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build()
        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field, 2)

        then:
        errorVal.message == "E2"
        errorArg == 2
    }

    def "when custom bus passed to builder it should be used instead of default"() {
        given:
        def sessionProv = { return "custom" } as Provider<String>
        EventBus customBus = Mock()
        EventBusCachedFieldWithArg field = EventBusCachedFieldWithArg.builder()
                .withValueProvider(MockFactory.getStringGetter(argToValueMap))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .withCustomSessionIdProvider(sessionProv)
                .withCustomBus(customBus)
                .build()

        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field, 1)

        then:
        1 * customBus.post(_)
        0 * bus.post(_)
    }
}
