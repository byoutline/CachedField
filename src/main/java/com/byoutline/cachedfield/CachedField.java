package com.byoutline.cachedfield;

/**
 * Field of which getting value takes time (because it is downloaded from remote
 * source, or needs heavy calculations), so it is wrapped for caching.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface CachedField<T> {

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
}
