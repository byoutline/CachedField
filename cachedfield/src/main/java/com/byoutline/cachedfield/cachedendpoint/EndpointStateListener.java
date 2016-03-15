package com.byoutline.cachedfield.cachedendpoint;

/**
 * Listener that will be informed when call state changes(ie: from loading to
 * loaded successfully).
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface EndpointStateListener<RETURN_TYPE, ARG_TYPE> {

    void endpointStateChanged(StateAndValue<RETURN_TYPE, ARG_TYPE> currentStateAndValue);
}
