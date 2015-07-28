package com.byoutline.cachedfield.cachedendpoint;

import com.byoutline.cachedfield.FieldState;
import com.byoutline.cachedfield.internal.StateAndValue;

/**
 * Wrapper for endpoint allowing executing calls from fragments/activities without leaking them.
 *
 * @param <RETURN_TYPE> Type of cached value.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE> {
    StateAndValue<RETURN_TYPE, ARG_TYPE> getStateAndValue();

    void call(ARG_TYPE arg);

    /**
     * Forget cached value, so memory can be reclaimed.
     */
    void drop();

    /**
     * Register listener that will be informed each time {@link FieldState}
     * changes.
     *
     * @param listener
     * @throws IllegalArgumentException if listener is null
     */
    void addEndpointListener(EndpointStateListener listener);

    /**
     * Remove field state listener
     *
     * @param listener
     * @return true if listeners collection was modified by this operation,
     * false otherwise
     * @throws IllegalArgumentException if listener is null
     */
    boolean removeEndpointListener(EndpointStateListener listener);
}
