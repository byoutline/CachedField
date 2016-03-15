package com.byoutline.cachedfield;

/**
 * Will be called when value is loaded.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface SuccessListener<T> {

    void valueLoaded(T value);
}
