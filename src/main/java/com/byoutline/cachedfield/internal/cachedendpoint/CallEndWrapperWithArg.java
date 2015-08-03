package com.byoutline.cachedfield.internal.cachedendpoint;

import com.byoutline.cachedfield.ErrorListenerWithArg;
import com.byoutline.cachedfield.SuccessListenerWithArg;
import com.byoutline.cachedfield.cachedendpoint.CallEndListener;
import com.byoutline.cachedfield.cachedendpoint.CallResult;
import com.byoutline.cachedfield.cachedendpoint.EndpointState;
import com.byoutline.cachedfield.cachedendpoint.StateAndValue;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CallEndWrapperWithArg<RETURN_TYPE, ARG_TYPE> implements SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE>, ErrorListenerWithArg<ARG_TYPE> {
    private final CallEndListener<RETURN_TYPE, ARG_TYPE> listener;

    public CallEndWrapperWithArg(CallEndListener<RETURN_TYPE, ARG_TYPE> listener) {
        this.listener = listener;
    }

    @Override
    public void valueLoadingFailed(Exception ex, ARG_TYPE arg) {
        CallResult<RETURN_TYPE> result = CallResult.<RETURN_TYPE>create(null, ex);
        listener.callEnded(StateAndValue.<RETURN_TYPE, ARG_TYPE>create(EndpointState.CALL_FAILED, result, arg));
    }

    @Override
    public void valueLoaded(RETURN_TYPE value, ARG_TYPE arg) {
        CallResult<RETURN_TYPE> result = CallResult.<RETURN_TYPE>create(value, null);
        listener.callEnded(StateAndValue.<RETURN_TYPE, ARG_TYPE>create(EndpointState.CALL_SUCCESS, result, arg));
    }
}
