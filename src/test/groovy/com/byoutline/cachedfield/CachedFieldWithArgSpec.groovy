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
}
