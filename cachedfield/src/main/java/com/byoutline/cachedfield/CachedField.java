package com.byoutline.cachedfield;

/**
 * Field of which getting value takes time (because it is downloaded from remote
 * source, or needs heavy calculations), so it is wrapped for caching.
 *
 * @param <RETURN_TYPE> Type of cached value.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface CachedField<RETURN_TYPE> {

    FieldState getState();

    /**
     * Informs {@link SuccessListener} when value is ready.
     */
    void postValue();

    /**
     * Force value to refresh(be fetched again from remote source or calculated
     * again).
     */
    void refresh();

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
     * Remove field state listener.
     *
     * @param listener
     * @return true if listeners collection was modified by this operation,
     * false otherwise
     * @throws IllegalArgumentException if listener is null
     */
    boolean removeStateListener(FieldStateListener listener);

    CachedFieldWithArg<RETURN_TYPE, Void> toCachedFieldWithArg();
}
