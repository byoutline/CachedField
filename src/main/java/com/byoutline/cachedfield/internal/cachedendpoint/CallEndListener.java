package com.byoutline.cachedfield.internal.cachedendpoint;

import com.byoutline.cachedfield.cachedendpoint.StateAndValue;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface CallEndListener<RETURN_TYPE, ARG_TYPE> {
    void callEnded(StateAndValue<RETURN_TYPE, ARG_TYPE> callResult);
}
