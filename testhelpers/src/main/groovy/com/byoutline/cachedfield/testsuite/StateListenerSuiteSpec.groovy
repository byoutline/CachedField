package com.byoutline.cachedfield.testsuite

import com.byoutline.cachedfield.CachedField
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.cachedfield.MockFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout

import javax.inject.Provider

import static com.byoutline.cachedfield.MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading
import static com.byoutline.cachedfield.MockCachedFieldLoader.refreshAndWaitUntilFieldStopsLoading

abstract class StateListenerSuiteSpec extends Specification {
    @Shared
    def value = "value"

    abstract def getField(Provider<String> valueProvider)

    @Timeout(1)
    def "should allow self removing state listeners"() {
        given:
        CachedField field = getField(MockFactory.getStringGetter(value))
        def stateListeners = [new SelfRemovingFieldStateListener(field),
                              new SelfRemovingFieldStateListener(field),
                              new SelfRemovingFieldStateListener(field)]
        stateListeners.each { field.addStateListener(it) }
        when:
        postAndWaitUntilFieldStopsLoading(field)
        then:
        stateListeners.findAll { it.called }.size() == 3
    }

    @Timeout(1)
    def "should inform field state listener about changes on refresh"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        def field = getField(MockFactory.getStringGetter(value))
        field.addStateListener(stateList)

        when:
        refreshAndWaitUntilFieldStopsLoading(field)

        then:
        postedStates == [FieldState.CURRENTLY_LOADING, FieldState.LOADED]
    }
}
