package com.byoutline.cachedfield.utils

import com.byoutline.cachedfield.CFMockFactory
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
        def field = CFMockFactory.getCachedFieldBlockingVal()
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
        def field = CFMockFactory.getCachedFieldWithArgBlockingVal()
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
        def field = CFMockFactory.getCachedEndpointBlockingVal()
        def instance = CachedFieldsListener.from(field)
        instance.setListener { listenerStates.add(it) }

        when:
        field.call(3)

        then:
        listenerStates == [true, false]
    }

    def "getRegisterCount should contain counts of fields"() {
        given:
        def instance = CachedFieldsListener.from(
                [CFMockFactory.getCachedFieldBlockingVal(), CFMockFactory.getCachedFieldBlockingVal(), CFMockFactory.getCachedFieldBlockingVal()],
                [CFMockFactory.getCachedFieldWithArgBlockingVal(), CFMockFactory.getCachedFieldWithArgBlockingVal()],
                [CFMockFactory.getCachedEndpointBlockingVal()]
        )

        when:
        def result = instance.getRegisterCount()

        then:
        result.contains('3')
        result.contains('2')
        result.contains('1')
    }

    def "unregisterFromFields should stop tracking fields"() {
        given:
        def listenerStates = []
        def field = CFMockFactory.getCachedFieldBlockingVal()
        def instance = CachedFieldsListener.from(field)
        instance.setListener { listenerStates.add(it) }

        when:
        instance.unregisterFromFields()

        then:
        field.postValue()
        listenerStates == []
    }
}
