package com.byoutline.cachedfield.cachedendpoint;

/**
 * Listener that will be called when call ends with either
 * {@link EndpointState#CALL_SUCCESS} or {@link EndpointState#CALL_FAILED}.
 *
 * @param <RETURN_TYPE> Type of value to be cached
 * @param <ARG_TYPE>    Argument needed to calculate value.
 */
public interface CallEndListener<RETURN_TYPE, ARG_TYPE> {
    void callEnded(StateAndValue<RETURN_TYPE, ARG_TYPE> callResult);
}
