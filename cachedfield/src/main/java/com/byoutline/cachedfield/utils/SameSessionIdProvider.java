package com.byoutline.cachedfield.utils;

import javax.inject.Provider;

/**
 * Implementation of session ID that always returns same value.
 * This can be used when application does not use concept of session
 * for invalidating cache.
 */
public class SameSessionIdProvider implements Provider<String> {
    @Override
    public String get() {
        return "";
    }
}
