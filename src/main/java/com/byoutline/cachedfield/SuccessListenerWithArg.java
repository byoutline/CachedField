package com.byoutline.cachedfield;

/**
 * Will be called when value is loaded.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 * @param <VALUE_TYPE> Type of loaded value.
 * @param <ARG_TYPE> Type of argument used to calculate/load value.
 */
public interface SuccessListenerWithArg<VALUE_TYPE, ARG_TYPE> {

    void valueLoaded(VALUE_TYPE value, ARG_TYPE arg);
}
