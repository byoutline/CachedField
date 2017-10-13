package com.byoutline.cachedfield

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedFieldWithArgSpec extends Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']

    def "should null out argument when drop is called"() {
        given:
        CachedFieldWithArg field = CFMockFactory.getCachedFieldWithArg(argToValueMap)
        CFMockFactory.loadValue(field, 1)

        when:
        field.drop()

        then:
        field.getState() == FieldState.NOT_LOADED
        field.value.arg == null
    }

    @Unroll
    def "should inform success listener about value: #val for argument: #arg"() {
        given:
        String result = 'fail'
        def successListener = { value, arg -> result = value } as SuccessListenerWithArg<String, Integer>
        CachedFieldWithArg field = CFMockFactory.getCachedFieldWithArg(argToValueMap, successListener)
        when:
        CFMockFactory.loadValue(field, arg)
        then:
        result == val
        where:
        arg | val
        0   | null
        1   | 'a'
        2   | 'b'
    }


    @Unroll
    def "should inform success listener about argument: #arg"() {
        given:
        int result = -1
        def successListener = { value, arg -> result = arg } as SuccessListenerWithArg<String, Integer>
        CachedFieldWithArg field = CFMockFactory.getCachedFieldWithArg(argToValueMap, successListener)
        when:
        CFMockFactory.loadValue(field, arg)
        then:
        result == arg
        where:
        arg << [0, 1, 2, 3, 12512]
    }

    def "should remove state listener"() {
        given:
        FieldState state = FieldState.NOT_LOADED
        def stateListener = { newState -> state = newState } as FieldStateListener
        CachedFieldWithArg field = CFMockFactory.getCachedFieldWithArg(argToValueMap)
        field.addStateListener(stateListener)
        when:
        field.removeStateListener(stateListener)
        CFMockFactory.loadValue(field, 1)
        then:
        state == FieldState.NOT_LOADED
    }

    def "should inform success listener only about last value if interrupted with new argument"() {
        given:
        def results = []
        def successListener = { value, arg -> results.add(value) } as SuccessListenerWithArg<String, Integer>

        CachedFieldWithArg field = new CachedFieldWithArgImpl(
                CFMockFactory.getSameSessionIdProvider(),
                CFMockFactory.getDelayedStringIntGetter(argToValueMap, 1),
                successListener,
                CFMockFactory.getErrorListenerWithArg()
        )
        when:
        field.postValue(1)
        CFMockFactory.loadValue(field, 2)
        then:
        results == ['b']
    }

    def "should inform error listener if interrupted with new argument"() {
        given:
        def resultArg = 'fail'
        def errorListener = { ex, arg -> resultArg = arg } as ErrorListenerWithArg<Integer>
        CachedFieldWithArg field = CFMockFactory.getCachedFieldWithArg(argToValueMap, errorListener)
        when:
        field.postValue(1)
        CFMockFactory.loadValue(field, 2)
        then:
        resultArg == 1
    }


    def "should have field state set to LOADED when success listener is informed"() {
        given:
        FieldState fieldState = null
        def successListener = new SuccessListenerWithArg<String, Integer>() {
            CachedFieldWithArg field

            @Override
            void valueLoaded(String value, Integer arg) {
                fieldState = field.getState()
            }
        }
        CachedFieldWithArg field = CFMockFactory.getCachedFieldWithArg(argToValueMap, successListener)
        successListener.field = field
        when:
        CFMockFactory.loadValue(field, 1)
        then:
        fieldState == FieldState.LOADED
    }
}

