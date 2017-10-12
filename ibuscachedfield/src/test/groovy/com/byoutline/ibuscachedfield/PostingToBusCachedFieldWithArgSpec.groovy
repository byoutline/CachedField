package com.byoutline.ibuscachedfield

import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.cachedfield.MockFactory
import com.byoutline.eventcallback.IBus
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Provider


/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class PostingToBusCachedFieldWithArgSpec extends Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']
    @Shared
    ResponseEventWithArg<String, Integer> successEvent
    ResponseEventWithArg<Exception, Integer> errorEvent
    IBus bus

    static <ARG_TYPE> void postAndWaitUntilFieldStopsLoading(CachedFieldWithArg<?, ARG_TYPE> field, ARG_TYPE arg) {
        boolean duringValueLoad = true
        def listener = { FieldState newState ->
            if (newState == FieldState.NOT_LOADED || newState == FieldState.LOADED) {
                duringValueLoad = false
            }
        } as FieldStateListener

        field.addStateListener(listener)
        field.postValue(arg)
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
    }

    @Unroll
    def "should post value: #val #text, times: #sC for arg: #arg"() {
        given:
        CachedFieldWithArg field = IBusMockFactory.fieldWithArgBuilder(bus)
                .withValueProvider(MockFactory.getStringGetter(argToValueMap))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build()
        when:
        postAndWaitUntilFieldStopsLoading(field, arg)

        then:

        sC * successEvent.setResponse(val, arg)
        0 * errorEvent.setResponse(_, _)

        where:
        val  | arg || sC
        null | 0   || 1
        'a'  | 1   || 1
        'b'  | 2   || 1
    }

    def "postValue should post error with argument"() {
        given:
        Exception errorVal = null
        Integer errorArg = null
        ResponseEventWithArg<Exception, Integer> errorEvent =
                { Exception val, Integer arg ->
                    errorVal = val; errorArg = arg
                } as ResponseEventWithArg<Exception, Integer>
        CachedFieldWithArg field = IBusMockFactory.fieldWithArgBuilder(bus)
                .withValueProvider(MockFactory.getFailingStringGetterWithArg())
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build()
        when:
        postAndWaitUntilFieldStopsLoading(field, 2)

        then:
        errorVal.message == "E2"
        errorArg == 2
    }

    def "builder should allow skipping error event"() {
        given:
        CachedFieldWithArg field = IBusMockFactory.fieldWithArgBuilder(bus)
                .withValueProvider(MockFactory.getStringGetter(argToValueMap))
                .withSuccessEvent(successEvent)
                .build()
        when:
        postAndWaitUntilFieldStopsLoading(field, 3)

        then:
        0 * errorEvent.setResponse(_, _)
    }

    def "custom bus passed to builder should be used instead of default"() {
        given:
        def sessionProv = { return "custom" } as Provider<String>
        IBus customBus = Mock()
        CachedFieldWithArg field = IBusMockFactory.fieldWithArgBuilder(bus)
                .withValueProvider(MockFactory.getStringGetter(argToValueMap))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .withCustomSessionIdProvider(sessionProv)
                .withCustomBus(customBus)
                .build()

        when:
        postAndWaitUntilFieldStopsLoading(field, 1)

        then:
        1 * customBus.post(_)
        0 * bus.post(_)
    }
}
