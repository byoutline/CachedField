package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.FieldState;

/**
 * Simple storage of value and state.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class StateAndValue<VALUE_TYPE, ARG_TYPE> {

    public final FieldState state;
    public final VALUE_TYPE value;
    public final ARG_TYPE arg;

    public StateAndValue(FieldState state, VALUE_TYPE value, ARG_TYPE arg) {
        this.state = state;
        this.value = value;
        this.arg = arg;
    }

    @Override
    public String toString() {
        return "StateAndValue{" + "state=" + state + ", value=" + value + ", arg=" + arg + '}';
    }
}
