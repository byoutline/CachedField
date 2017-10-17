package com.byoutline.cachedfield.testsuite

import com.byoutline.cachedfield.CachedField
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class SelfRemovingFieldStateListener implements FieldStateListener {
    final CachedField field
    boolean called = false

    SelfRemovingFieldStateListener(CachedField field) {
        this.field = field
    }

    @Override
    void fieldStateChanged(FieldState newState) {
        System.out.println("Called")
        called = true
        field.removeStateListener(this)
    }
}