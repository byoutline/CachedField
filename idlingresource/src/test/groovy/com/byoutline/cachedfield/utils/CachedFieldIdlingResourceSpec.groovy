package com.byoutline.cachedfield.utils

import com.byoutline.cachedfield.*
import com.byoutline.cachedfield.cachedendpoint.StateAndValue
import spock.lang.Specification
import spock.lang.Timeout
import spock.lang.Unroll

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedFieldIdlingResourceSpec extends Specification {

    @Timeout(1)
    @Unroll
    def "#name should return correct Idle status"() {
        given:
        def instance = CachedFieldIdlingResource.from(field)
        def transitionToIdle = false
        instance.registerIdleTransitionCallback { transitionToIdle = true }
        def idleBeforeCall = instance.idleNow

        when:
        field.setState(FieldState.CURRENTLY_LOADING)
        def idleDuringCall = instance.idleNow
        field.setState(FieldState.LOADED)
        def idleAfterCall = instance.idleNow

        then:
        idleBeforeCall
        !idleDuringCall
        idleAfterCall

        where:
        field               | name
        new MockVanillaCF() | "vanilla"
        new MockCFWithArg() | "with arg"
    }
}

class MockCFWithArg implements CachedFieldWithArg<String, Integer> {

    private FieldState state = FieldState.NOT_LOADED
    FieldStateListener listener = null

    @Override
    FieldState getState() {
        return state
    }

    void setState(FieldState state) {
        this.state = state
        listener?.fieldStateChanged(state)
    }

    @Override
    void postValue(Integer arg) {
    }

    @Override
    void refresh(Integer arg) {
    }

    @Override
    StateAndValue<String, Integer> getStateAndValue() {
        return StateAndValue.create(state, "", 1)
    }

    @Override
    void drop() {
    }

    @Override
    void addStateListener(FieldStateListener listener) {
        this.listener = listener
    }

    @Override
    boolean removeStateListener(FieldStateListener listener) {
        if (this.listener == listener) {
            this.listener = null
            return true
        }
        return false
    }
}

class MockVanillaCF implements CachedField<String> {
    private delegate = new MockCFWithArg()

    @Override
    FieldState getState() {
        return delegate.getState()
    }

    void setState(FieldState state) {
        delegate.setState(state)
    }

    @Override
    void postValue() {
    }

    @Override
    void refresh() {
    }

    @Override
    void drop() {
    }

    @Override
    void addStateListener(FieldStateListener listener) {
        delegate.addStateListener(listener)
    }

    @Override
    boolean removeStateListener(FieldStateListener listener) {
        delegate.removeStateListener(listener)
    }

    @Override
    CachedFieldWithArgImpl<String, Void> toCachedFieldWithArg() {
        return null
    }
}
