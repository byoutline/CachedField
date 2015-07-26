package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.cachedendpoint.CallResult;
import com.byoutline.cachedfield.cachedendpoint.EndpointState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Simple storage of value and state.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class StateAndValue<VALUE_TYPE, ARG_TYPE> {

    @Nonnull
    public final EndpointState state;
    @Nonnull
    public final CallResult<VALUE_TYPE> value;
    @Nullable
    public final ARG_TYPE arg;

    public StateAndValue(@Nonnull EndpointState state, @Nonnull CallResult<VALUE_TYPE> value, @Nullable ARG_TYPE arg) {
        this.state = state;
        this.value = value;
        this.arg = arg;
    }

    @Override
    public String toString() {
        return "StateAndValue{" + "state=" + state + ", value=" + value + ", arg=" + arg + '}';
    }
}
