package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.ErrorListenerWithArg;

/**
 * {@link ErrorListenerWithArg} that does nothing.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class StubErrorListenerWithArg<ARG_TYPE> implements ErrorListenerWithArg<ARG_TYPE> {

    @Override
    public void valueLoadingFailed(Exception ex, ARG_TYPE arg) {
        // ignore
    }
}
