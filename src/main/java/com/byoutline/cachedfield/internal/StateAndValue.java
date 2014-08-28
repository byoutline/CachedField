package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.FieldState;

/**
 * Simple storage of value and state.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class StateAndValue<T> {

    public final FieldState state;
    public final T value;

    public StateAndValue(FieldState state, T value) {
        this.state = state;
        this.value = value;
    }

    @Override
    public String toString() {
        return "StateAndValue{" + "state=" + state + ", value=" + value + '}';
    }
}
