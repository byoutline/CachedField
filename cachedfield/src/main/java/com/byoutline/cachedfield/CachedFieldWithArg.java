package com.byoutline.cachedfield;

import com.byoutline.cachedfield.cachedendpoint.StateAndValue;

/**
 * Field of which getting value takes time (because it is downloaded from remote
 * source, or needs heavy calculations), so it is wrapped for caching.
 *
 * @param <RETURN_TYPE> Type of cached value.
 * @param <ARG_TYPE>    Argument needed to calculate value.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface CachedFieldWithArg<RETURN_TYPE, ARG_TYPE> {

    FieldState getState();

    /**
     * Informs {@link SuccessListener} when value is ready.
     *
     * @param arg Argument needed to calculate value.
     */
    void postValue(ARG_TYPE arg);

    /**
     * Force value to refresh(be fetched again from remote source or calculated
     * again).
     *
     * @param arg Argument needed to calculate value.
     */
    void refresh(ARG_TYPE arg);

    StateAndValue<RETURN_TYPE, ARG_TYPE> getStateAndValue();

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
    void addStateListener(FieldStateListener listener);

    /**
     * Remove field state listener
     *
     * @param listener
     * @return true if listeners collection was modified by this operation,
     * false otherwise
     * @throws IllegalArgumentException if listener is null
     */
    boolean removeStateListener(FieldStateListener listener);
}
