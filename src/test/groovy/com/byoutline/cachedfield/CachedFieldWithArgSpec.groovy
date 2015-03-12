package com.byoutline.cachedfield

import spock.lang.Shared

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
}
