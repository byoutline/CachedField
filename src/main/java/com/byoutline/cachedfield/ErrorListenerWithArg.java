package com.byoutline.cachedfield;

/**
 * Will be called when value loading fails.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 * @param <ARG_TYPE> Type of argument used to calculate/load value.
 */
public interface ErrorListenerWithArg<ARG_TYPE> {

    void valueLoadingFailed(Exception ex, ARG_TYPE arg);
}
