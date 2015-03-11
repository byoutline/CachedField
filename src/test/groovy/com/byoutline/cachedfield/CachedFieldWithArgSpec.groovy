package com.byoutline.cachedfield

import spock.lang.Shared

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedFieldWithArgSpec extends spock.lang.Specification {
    @Shared
    String value = "value"
    SuccessListener<String> stubSuccessListener = {} as SuccessListener<String>

    def "should null out argument when drop is called"() {
        given:
        SuccessListenerWithArg<String, Integer> tmp = MockFactory.getSuccessListenerWithArg()
        CachedFieldWithArg field = MockFactory.getCachedFieldWithArg([1: 'a'])
        MockFactory.loadValue(field, 1)

        when:
        field.drop()

        then:
        field.getState() == FieldState.NOT_LOADED
        field.value.arg == null
    }
}
