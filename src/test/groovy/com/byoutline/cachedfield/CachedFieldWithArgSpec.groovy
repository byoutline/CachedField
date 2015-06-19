package com.byoutline.cachedfield

import spock.lang.Shared
import spock.lang.Unroll

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedFieldWithArgSpec extends spock.lang.Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']

    def "should null out argument when drop is called"() {
        given:
        CachedFieldWithArg field = MockFactory.getCachedFieldWithArg(argToValueMap)
        MockFactory.loadValue(field, 1)

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
        CachedFieldWithArg field = MockFactory.getCachedFieldWithArg(argToValueMap, successListener)
        when:
        MockFactory.loadValue(field, arg)
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
        CachedFieldWithArg field = MockFactory.getCachedFieldWithArg(argToValueMap, successListener)
        when:
        MockFactory.loadValue(field, arg)
        then:
        result == arg
        where:
        arg << [0, 1, 2, 3, 12512]
    }

    def "should remove state listener"() {
        given:
        FieldState state = FieldState.NOT_LOADED
        def stateListener = { newState -> state = newState } as FieldStateListener
        CachedFieldWithArg field = MockFactory.getCachedFieldWithArg(argToValueMap)
        field.addStateListener(stateListener)
        when:
        field.removeStateListener(stateListener)
        MockFactory.loadValue(field, 1)
        then:
        state == FieldState.NOT_LOADED
    }

    def "should inform success listener only about last value if interrupted with new argument"() {
        given:
        def results = []
        def successListener = { value, arg -> results.add(value) } as SuccessListenerWithArg<String, Integer>

        CachedFieldWithArg field = new CachedFieldWithArgImpl(
                MockFactory.getSameSessionIdProvider(),
                MockFactory.getDelayedStringIntGetter(argToValueMap, 1),
                successListener,
                MockFactory.getErrorListenerWithArg()
        )
        when:
        field.postValue(1)
        MockFactory.loadValue(field, 2)
        then:
        results == ['b']
    }

    def "should inform error listener if interrupted with new argument"() {
        given:
        def resultArg = 'fail'
        def errorListener = { ex, arg -> resultArg = arg } as ErrorListenerWithArg<Integer>
        CachedFieldWithArg field = MockFactory.getCachedFieldWithArg(argToValueMap, errorListener)
        when:
        field.postValue(1)
        MockFactory.loadValue(field, 2)
        then:
        resultArg == 1
    }

    def "should allow self removing state listeners"() {
        given:
        Exception exception = null
        def errorListener = { ex, arg -> exception = ex } as ErrorListenerWithArg<Integer>
        CachedFieldWithArg field = MockFactory.getCachedFieldWithArg(argToValueMap, errorListener)
        def stateListeners = [new SelfRemovingFieldStateListener(field),
                              new SelfRemovingFieldStateListener(field),
                              new SelfRemovingFieldStateListener(field)]
        stateListeners.each { field.addStateListener(it) }
        when:
        MockFactory.loadValue(field, 1)
        then:
        exception == null
        stateListeners.findAll { it.called }.size() == 3
    }
}

