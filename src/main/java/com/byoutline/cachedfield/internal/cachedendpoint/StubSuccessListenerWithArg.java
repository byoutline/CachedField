package com.byoutline.cachedfield.internal.cachedendpoint;

import com.byoutline.cachedfield.SuccessListenerWithArg;

public class StubSuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> implements SuccessListenerWithArg<RETURN_TYPE,ARG_TYPE> {
    @Override
    public void valueLoaded(RETURN_TYPE value, ARG_TYPE arg) {
        // ignore
    }
}
