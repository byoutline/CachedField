package com.byoutline.cachedfield

import com.byoutline.cachedfield.internal.DefaultExecutors
import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Shared

import javax.inject.Provider

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedFieldSpec extends spock.lang.Specification {
    @Shared
    String value = "value"
    SuccessListener<String> stubSuccessListener = {} as SuccessListener<String>


    def "postValue should return immediately"() {
        given:
        CachedField field = MockFactory.getDelayedCachedField(value, 1000, stubSuccessListener)

        when:
        boolean tookToLong = false
        Thread.start {
            sleep 15
            tookToLong = true;
        }
        field.postValue()

        then:
        if (tookToLong) {
            throw new AssertionError("Test took to long to execute")
        }
    }


    def "should post success event immediately if it was loaded"() {
        given:
        CachedField field = MockFactory.getDelayedCachedField(value, 20, stubSuccessListener)
        field.postValue()
        MockFactory.waitUntilFieldLoads(field)

        when:
        boolean tookToLong = false
        Thread.start {
            sleep 15
            tookToLong = true;
        }
        field.postValue()

        then:
        if (tookToLong) {
            throw new AssertionError("Test took to long to execute")
        }
    }

    def "should inform success listener when loaded"() {
        given:
        boolean valuePosted = false
        def successList = { valuePosted = true } as SuccessListener<String>
        CachedField field = MockFactory.getDelayedCachedField(value, successList)

        when:
        field.postValue()
        MockFactory.waitUntilFieldLoads(field)

        then:
        assert valuePosted
    }

    def "should null out value when drop is called"() {
        given:
        CachedField field = MockFactory.getLoadedCachedField(value)

        when:
        field.drop()

        then:
        field.getState() == FieldState.NOT_LOADED
        field.delegate.value.successValue == null
        field.delegate.value.errorValue == null
    }

    def "should inform field state listener about changes on postValue"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        CachedField field = MockFactory.getDelayedCachedField(value, stateList)

        when:
        field.postValue()
        MockFactory.waitUntilFieldLoads(field)

        then:
        postedStates == [FieldState.CURRENTLY_LOADING, FieldState.LOADED]
    }

    def "should inform field state listener about changes on drop"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        CachedField field = MockFactory.getLoadedCachedField(value, stateList)

        when:
        field.drop()

        then:
        postedStates == [FieldState.NOT_LOADED]
    }


    def "should inform field state listener about changes on refresh"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        CachedField field = MockFactory.getLoadedCachedField(value, stateList)

        when:
        field.refresh()
        sleep 1
        MockFactory.waitUntilFieldLoads(field)

        then:
        postedStates == [FieldState.CURRENTLY_LOADING, FieldState.LOADED]
    }

    def "should inform field state listener about changes on session expire"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        def currentSession = "one"
        def sessionProvider = { return currentSession } as Provider<String>
        CachedField field = MockFactory.getLoadedCachedField(value, stateList, sessionProvider)

        when:
        // Asking for state will force CachedField to check its current state
        // without us forcing it to change. This way only expired session can
        // cause state change.
        currentSession = "two"
        field.getState()

        then:
        postedStates == [FieldState.NOT_LOADED]
    }

    def "should inform error listener if value getter throws exception"() {
        given:
        def exceptionThrown = new RuntimeException()
        Exception resultEx = null;
        def valueProv = { throw exceptionThrown } as Provider<String>
        def errorList = { resultEx = it } as ErrorListener
        CachedField field = new CachedFieldImpl(
                MockFactory.getSameSessionIdProvider(),
                valueProv,
                MockFactory.getSuccessListener(),
                errorList,
                MoreExecutors.newDirectExecutorService(),
                DefaultExecutors.createDefaultStateListenerExecutor()
        )

        when:
        field.postValue()

        then:
        resultEx == exceptionThrown
    }


    def "should inform error listener if success listener throws exception"() {
        given:
        Exception exceptionThrown = new RuntimeException()
        Exception resultEx = null
        def successListener = {throw exceptionThrown} as SuccessListener<String>
        def errorListener = { resultEx = it } as ErrorListener
        CachedField field = new CachedFieldImpl(
                MockFactory.getSameSessionIdProvider(),
                MockFactory.getDelayedStringGetter(value, 2),
                successListener,
                errorListener,
                MoreExecutors.newDirectExecutorService(),
                DefaultExecutors.createDefaultStateListenerExecutor()
        )
        when:
        field.postValue()

        then:
        resultEx == exceptionThrown
    }

    def "should allow self removing state listeners"() {
        given:
        Exception resultEx = null
        def errorListener = { resultEx = it } as ErrorListener
        CachedField field = MockFactory.getCachedField(value, errorListener)
        def stateListeners = [new SelfRemovingFieldStateListener(field),
                              new SelfRemovingFieldStateListener(field),
                              new SelfRemovingFieldStateListener(field)]
        stateListeners.each { field.addStateListener(it) }
        when:
        field.postValue()
        MockFactory.waitUntilFieldLoads(field)
        then:
        resultEx == null
        stateListeners.findAll { it.called }.size() == 3
    }

    def "should call success once if asked about value during load"() {
        given:
        int callCount = 0
        def successListener = {callCount++} as SuccessListener<String>
        CachedField field = new CachedFieldImpl(
                MockFactory.getSameSessionIdProvider(),
                MockFactory.getDelayedStringGetter(value),
                successListener
        )
        when:
        field.postValue()
        MockFactory.waitUntilFieldReachesState(field, FieldState.CURRENTLY_LOADING)
        field.postValue()
        MockFactory.waitUntilFieldLoads(field)
        then:
        callCount == 1
    }
}
