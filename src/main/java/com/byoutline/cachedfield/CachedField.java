package com.byoutline.cachedfield;

/**
 * Field of which getting value takes time 
 * (because it is downloaded from remote source, or needs heavy calculations), 
 * so it is wrapped for caching.
 *
 * @author Sebastian Kacprzak <nait at naitbit.com>
 */
public interface CachedField<T> {

    FieldState getState();

    /**
     * Posts value on Bus when it is ready. 
     */
    void postValue();

    /**
     * Force value to refresh(be fetched again from remote source or calculated again).
     */
    void refresh();
}
