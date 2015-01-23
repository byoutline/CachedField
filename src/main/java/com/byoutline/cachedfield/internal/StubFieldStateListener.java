package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.FieldState;
import com.byoutline.cachedfield.FieldStateListener;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class StubFieldStateListener implements FieldStateListener {

    @Override
    public void fieldStateChanged(FieldState newState) {
        // Ignore.
    }

}
