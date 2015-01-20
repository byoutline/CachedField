package com.byoutline.cachedfield;

/**
 * Listener that will be informed when field state changes(ie: from LOADING to
 * LOADED).
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface FieldStateListener {

    void fieldStateChanged(FieldState newState);
}
