package com.byoutline.cachedfield;

/**
 * Will be called when value loading fails.
 *
 * @param <ARG_TYPE> Type of argument used to calculate/load value.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface ErrorListenerWithArg<ARG_TYPE> {

    void valueLoadingFailed(Exception ex, ARG_TYPE arg);
}
