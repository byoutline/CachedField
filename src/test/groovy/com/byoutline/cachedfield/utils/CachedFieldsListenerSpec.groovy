package com.byoutline.cachedfield.utils

import com.byoutline.cachedfield.MockFactory
import spock.lang.Shared
import spock.lang.Specification

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedFieldsListenerSpec extends Specification {

    @Shared
    String value = "value"

    def "no args should notify about field starting to load"() {
        given:
        def listenerStates = []
        def field = MockFactory.getCachedFieldBlockingVal()
        def instance = CachedFieldsListener.from(field)
        instance.setListener { listenerStates.add(it) }

        when:
        field.postValue()

        then:
        listenerStates == [true, false]
    }

    def "with args should notify about field starting to load"() {
        given:
        def listenerStates = []
        def field = MockFactory.getCachedFieldWithArgBlockingVal()
        def instance = CachedFieldsListener.from(field)
        instance.setListener { listenerStates.add(it) }

        when:
        field.postValue(3)

        then:
        listenerStates == [true, false]
    }

    def "endpoint should notify about field starting to load"() {
        given:
        def listenerStates = []
        def field = MockFactory.getCachedFieldWithArgBlockingVal()
        def instance = CachedFieldsListener.from(field)
        instance.setListener { listenerStates.add(it) }

        when:
        field.postValue(3)

        then:
        listenerStates == [true, false]
    }
}
