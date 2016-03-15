package com.byoutline.cachedfield.internal.cachedendpoint;

import com.byoutline.cachedfield.FieldState;
import com.byoutline.cachedfield.FieldStateListener;
import com.byoutline.cachedfield.cachedendpoint.EndpointState;
import com.byoutline.cachedfield.cachedendpoint.EndpointStateListener;
import com.byoutline.cachedfield.cachedendpoint.StateAndValue;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class FieldStateListenerWrapper<RETURN_TYPE, ARG_TYPE> implements EndpointStateListener<RETURN_TYPE, ARG_TYPE> {

    public static <RETURN_TYPE, ARG_TYPE> FieldStateListenerWrapper<RETURN_TYPE, ARG_TYPE> create(
            @Nonnull FieldStateListener delegate) {
        return new AutoValue_FieldStateListenerWrapper<RETURN_TYPE, ARG_TYPE>(delegate);
    }

    @Override
    public void endpointStateChanged(StateAndValue currentState) {
        FieldState state = EndpointState.toFieldState(currentState.getState());
        getDelegate().fieldStateChanged(state);
    }

    abstract FieldStateListener getDelegate();
}
