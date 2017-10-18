package com.byoutline.cachedfield.testsuite

import com.byoutline.cachedfield.CachedField
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.cachedendpoint.CallResult
import com.byoutline.cachedfield.cachedendpoint.EndpointState
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout

import javax.inject.Provider
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

import static com.byoutline.cachedfield.MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading
import static com.byoutline.cachedfield.MockCachedFieldLoader.refreshAndWaitUntilFieldStopsLoading

abstract class CachedFieldCommonSuiteSpec extends Specification {
    @Shared
    def value = "value"

    abstract
    def getField(Provider<String> valueProvider, ExecutorService valueGetterExecutor, Executor stateListenerExecutor)

    def getFieldWithDefaultExecutors(Provider<String> valueProvider) {
        return getField(valueProvider, DefaultExecutors.createDefaultValueGetterExecutor(),
                DefaultExecutors.createDefaultStateListenerExecutor())
    }

    @Timeout(1)
    def "should allow self removing state listeners"() {
        given:
        CachedField field = getFieldWithDefaultExecutors(MockFactory.getStringGetter(value))
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
        def field = getFieldWithDefaultExecutors(MockFactory.getStringGetter(value))
        field.addStateListener(stateList)

        when:
        refreshAndWaitUntilFieldStopsLoading(field)

        then:
        postedStates == [FieldState.CURRENTLY_LOADING, FieldState.LOADED]
    }

    @Timeout(1)
    def "should inform field state listener about changes on postValue"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        def field = getFieldWithDefaultExecutors(MockFactory.getStringGetter(value))
        field.addStateListener(stateList)

        when:
        postAndWaitUntilFieldStopsLoading(field)

        then:
        postedStates == [FieldState.CURRENTLY_LOADING, FieldState.LOADED]
    }

    @Timeout(1)
    def "postValue should return immediately"() {
        given:
        def field = getFieldWithDefaultExecutors(MockFactory.getDelayedStringGetter(value, 2000))

        when:
        boolean tookToLong = false
        Thread.start {
            sleep 15
            tookToLong = true
        }
        field.postValue()

        then:
        if (tookToLong) {
            throw new AssertionError("Test took to long to execute")
        }
    }

    def "should inform field state listener about changes on drop"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        CachedField field = getField(MockFactory.getStringGetter(value),
                MoreExecutors.newDirectExecutorService(),
                MoreExecutors.directExecutor())
        field.postValue()
        field.addStateListener(stateList)

        when:
        field.drop()

        then:
        def stateAndValue = field.toCachedFieldWithArg().getStateAndValue()
        postedStates == [FieldState.NOT_LOADED]
        stateAndValue.state == EndpointState.BEFORE_CALL
        stateAndValue.value == CallResult.create(null, null)
        stateAndValue.arg == null
    }
}
