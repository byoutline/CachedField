package com.byoutline.cachedfield

import com.byoutline.cachedfield.cachedendpoint.EndpointStateListener
import com.byoutline.cachedfield.cachedendpoint.StateAndValue

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class StubCachedEndpointWithArg implements EndpointStateListener<String, Integer> {
    def postedStates = []
    def postedSuccess = []
    def postedFailure = []
    def postedArgs = []

    @Override
    void endpointStateChanged(StateAndValue<String, Integer> newState) {
        postedStates.add(newState.state)
        postedSuccess.add(newState.value.successResult)
        postedFailure.add(newState.value.failureResult)
        postedArgs.add(newState.arg)
    }

    void clear() {
        postedStates.clear()
        postedSuccess.clear()
        postedFailure.clear()
        postedArgs.clear()
    }
}
