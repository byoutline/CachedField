package com.byoutline.cachedfield

import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArgImpl
import com.byoutline.cachedfield.cachedendpoint.EndpointState
import com.byoutline.cachedfield.cachedendpoint.EndpointStateListener
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Shared

import javax.inject.Provider

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedEndpointWithArgSpec extends spock.lang.Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']

    def "should null out argument when drop is called"() {
        given:
        CachedEndpointWithArg field = MockFactory.getCachedEndpointBlockingValueProv(argToValueMap)
        field.call(1)

        when:
        field.drop()

        then:
        def stateAndValue = field.getStateAndValue()
        stateAndValue.state == EndpointState.BEFORE_CALL
        stateAndValue.value.failureResult == null
        stateAndValue.value.successResult == null
    }


    def "should remove state listener"() {
        given:
        EndpointState state = EndpointState.BEFORE_CALL
        def stateListener = { newState -> state = newState.getState() } as EndpointStateListener<String, Integer>
        CachedEndpointWithArg field = MockFactory.getCachedEndpointBlockingValueProv(argToValueMap)
        field.addEndpointListener(stateListener)
        when:
        field.removeEndpointListener(stateListener)
        field.call(1)
        then:
        state == EndpointState.BEFORE_CALL
    }

    def "call should return immediately"() {
        given:
        CachedEndpointWithArg field = MockFactory.getCachedEndpoint(argToValueMap)

        when:
        boolean tookToLong = false
        Thread.start {
            sleep 15
            tookToLong = true;
        }
        field.call(1)

        then:
        if (tookToLong) {
            throw new AssertionError("Test took to long to execute")
        }
    }

    def "should inform endpoint state listener about current state"() {
        given:
        def stateList = new StubCachedEndpointWithArg()
        CachedEndpointWithArg field = MockFactory.getCachedEndpointBlocking(argToValueMap)

        when:
        field.addEndpointListener(stateList)

        then:
        stateList.postedStates == [EndpointState.BEFORE_CALL]
        stateList.postedSuccess == [null]
        stateList.postedFailure == [null]
        stateList.postedArgs == [null]
    }

    def "should inform endpoint state listener about changes on call"() {
        given:
        def stateList = new StubCachedEndpointWithArg()
        CachedEndpointWithArg field = MockFactory.getCachedEndpointBlockingValueProv(argToValueMap)
        field.addEndpointListener(stateList)
        stateList.clear()

        when:
        field.call(1)

        then:
        stateList.postedStates == [EndpointState.DURING_CALL, EndpointState.CALL_SUCCESS]
        stateList.postedSuccess == [null, 'a']
        stateList.postedFailure == [null, null]
        stateList.postedArgs == [1, 1]
    }

    def "should inform endpoint state listener about changes on drop"() {
        given:
        CachedEndpointWithArg field = MockFactory.getCachedEndpointBlocking(argToValueMap)
        def stateList = new StubCachedEndpointWithArg()
        field.call(1)
        field.addEndpointListener(stateList)
        stateList.clear()

        when:
        field.drop()

        then:
        stateList.postedStates == [EndpointState.BEFORE_CALL]
        stateList.postedSuccess == [null]
        stateList.postedFailure == [null]
        stateList.postedArgs == [null]
    }

    def "should inform endpoint state listener about changes on session expire"() {
        given:
        def currentSession = "one"
        def sessionProvider = { return currentSession } as Provider<String>
        CachedEndpointWithArg field = new CachedEndpointWithArgImpl(
                sessionProvider,
                MockFactory.getStringIntGetter(argToValueMap),
                MoreExecutors.newDirectExecutorService(),
                DefaultExecutors.createDefaultStateListenerExecutor()
        )
        def stateList = new StubCachedEndpointWithArg()
        field.call(1)
        field.addEndpointListener(stateList)
        stateList.clear()

        when:
        // Asking for state will force CachedField to check its current state
        // without us forcing it to change. This way only expired session can
        // cause state change.
        currentSession = "two"
        field.getStateAndValue()

        then:
        stateList.postedStates == [EndpointState.BEFORE_CALL]
    }

    def "should not allow adding null state listeners"() {
        given:
        def field = MockFactory.getCachedEndpointBlocking(argToValueMap)
        when:
        field.addEndpointListener(null)
        then:
        thrown IllegalArgumentException
    }

    def "should not allow removing null state listeners"() {
        given:
        def field = MockFactory.getCachedEndpointBlocking(argToValueMap)
        when:
        field.removeEndpointListener(null)
        then:
        thrown IllegalArgumentException
    }
}

