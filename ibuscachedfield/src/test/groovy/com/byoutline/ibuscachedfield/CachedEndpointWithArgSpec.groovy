package com.byoutline.ibuscachedfield

import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg
import com.byoutline.cachedfield.cachedendpoint.CallResult
import com.byoutline.cachedfield.cachedendpoint.EndpointState
import com.byoutline.cachedfield.cachedendpoint.StateAndValue
import com.byoutline.eventcallback.IBus
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg
import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedEndpointWithArgSpec extends Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']
    @Shared
    ResponseEventWithArg<StateAndValue<String, Integer>, Integer> resultEvent
    IBus bus

    def setup() {
        bus = Mock()
        resultEvent = Mock()
    }

    @Unroll
    def "should post value: #val , times: #sC for arg: #arg"() {
        given:
        CachedEndpointWithArg field = IBusMockFactory.endpointWithArgBuilder(bus)
                .withValueProvider(MockFactory.getStringGetter(argToValueMap))
                .withResultEvent(resultEvent)
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .build()
        when:
        field.call(arg)

        then:

        sC * resultEvent.setResponse(
                StateAndValue.create(EndpointState.CALL_SUCCESS,
                        CallResult.create(val, null), arg), arg)

        where:
        val  | arg | sC
        null | 0   | 1
        'a'  | 1   | 1
        'b'  | 2   | 1
    }

    def "postValue should post error with argument"() {
        given:
        CachedEndpointWithArg field = IBusMockFactory.endpointWithArgBuilder(bus)
                .withValueProvider(MockFactory.getFailingStringGetterWithArg())
                .withResultEvent(resultEvent)
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .build()
        when:
        field.call(2)

        then:
        def result = field.getStateAndValue()
        result.state == EndpointState.CALL_FAILED
        result.arg == 2
        result.value.successResult == null
        result.value.failureResult.getMessage() == "E2"
    }
}
