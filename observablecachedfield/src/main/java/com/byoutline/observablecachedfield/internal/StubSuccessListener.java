package com.byoutline.observablecachedfield.internal;

import com.byoutline.cachedfield.SuccessListener;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class StubSuccessListener<RETURN_TYPE> implements SuccessListener<RETURN_TYPE> {
    @Override
    public void valueLoaded(RETURN_TYPE return_type) {
    }
}
