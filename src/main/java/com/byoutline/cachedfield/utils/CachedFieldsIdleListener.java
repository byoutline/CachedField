package com.byoutline.cachedfield.utils;

/**
 * Listener will be informed when any of the fields will start or stop loading.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface CachedFieldsIdleListener {
    void onFieldsStateChange(boolean currentlyLoading);
}
