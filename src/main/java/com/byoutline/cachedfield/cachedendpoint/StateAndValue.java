package com.byoutline.cachedfield.cachedendpoint;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Simple storage of value and state. Equals and hashcode compares all values.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
@AutoValue
public abstract class StateAndValue<VALUE_TYPE, ARG_TYPE> {

    public static <VALUE_TYPE, ARG_TYPE> StateAndValue<VALUE_TYPE, ARG_TYPE> create(@Nonnull EndpointState state,
                                                                                    @Nonnull CallResult<VALUE_TYPE> value,
                                                                                    @Nullable ARG_TYPE arg) {
        return new AutoValue_StateAndValue<VALUE_TYPE, ARG_TYPE>(state, value, arg);
    }

    @Nonnull
    public abstract EndpointState getState();

    @Nonnull
    public abstract CallResult<VALUE_TYPE> getValue();

    @Nullable
    public abstract ARG_TYPE getArg();
}
