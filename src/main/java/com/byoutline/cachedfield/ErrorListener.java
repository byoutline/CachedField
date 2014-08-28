package com.byoutline.cachedfield;

/**
 * Will be called when value loading fails.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface ErrorListener {

    void valueLoadingFailed(Exception ex);
}
