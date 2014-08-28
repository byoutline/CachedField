package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.ErrorListener;

/**
 * {@link ErrorListener} that does nothing.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class StubErrorListener implements ErrorListener {

    @Override
    public void valueLoadingFailed(Exception ex) {
        // ignore
    }
}
