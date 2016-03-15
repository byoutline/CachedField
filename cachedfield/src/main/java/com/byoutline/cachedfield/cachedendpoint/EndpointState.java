package com.byoutline.cachedfield.cachedendpoint;

import com.byoutline.cachedfield.FieldState;

public enum EndpointState {
    BEFORE_CALL, DURING_CALL, CALL_SUCCESS, CALL_FAILED;

    public static FieldState toFieldState(EndpointState state) {
        switch (state) {
            case BEFORE_CALL:
                return FieldState.NOT_LOADED;
            case DURING_CALL:
                return FieldState.CURRENTLY_LOADING;
            case CALL_SUCCESS:
                return FieldState.LOADED;
            case CALL_FAILED:
                return FieldState.NOT_LOADED;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}
