package com.byoutline.ottocachedfield

import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.cachedfield.MockCachedFieldLoader
import com.byoutline.cachedfield.MockFactory
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg
import com.squareup.otto.Bus
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Provider

import static com.byoutline.ottocachedfield.OttoMockFactory.ottoWithArgBuilder

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
    Bus bus

    def setup() {
        bus = Mock()
        successEvent = Mock()
        errorEvent = Mock()

        OttoCachedField.init(MockFactory.getSameSessionIdProvider(), bus)
    }

    @Unroll
    def "should post value: #val #text, times: #sC for arg: #arg when created by: #builder.class.simpleName"() {
        given:
        CachedFieldWithArg field = builder()
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
        val  | arg | builder                  || sC
        null | 0   | { ottoWithArgBuilder() } || 1
        'a'  | 1   | { ottoWithArgBuilder() } || 1
        'b'  | 2   | { ottoWithArgBuilder() } || 1
    }

    @Unroll
    def "postValue should post error with argument when created by: #builder.class.simpleName"() {
        given:
        Exception errorVal = null
        Integer errorArg = null
        ResponseEventWithArg<Exception, Integer> errorEvent =
                { Exception val, Integer arg ->
                    errorVal = val; errorArg = arg
                } as ResponseEventWithArg<Exception, Integer>
        CachedFieldWithArg field = builder
                .withValueProvider(MockFactory.getFailingStringGetterWithArg())
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build()
        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field, 2)

        then:
        errorVal.message == "E2"
        errorArg == 2

        where:
        builder << OttoMockFactory.busCachedFieldsWithArgBuilders()
    }

    @Unroll
    def "custom bus passed to builder should be used instead of default when created by: #builder.class.simpleName"() {
        given:
        def sessionProv = { return "custom" } as Provider<String>
        Bus customBus = Mock()
        CachedFieldWithArg field = builder
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

        where:
        builder << OttoMockFactory.busCachedFieldsWithArgBuilders()
    }
}
