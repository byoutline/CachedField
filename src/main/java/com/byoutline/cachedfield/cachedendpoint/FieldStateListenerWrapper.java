package com.byoutline.cachedfield.cachedendpoint;

import com.byoutline.cachedfield.FieldState;
import com.byoutline.cachedfield.FieldStateListener;
import com.byoutline.cachedfield.internal.StateAndValue;

import javax.annotation.Nonnull;

public class FieldStateListenerWrapper<RETURN_TYPE, ARG_TYPE> implements EndpointStateListener<RETURN_TYPE, ARG_TYPE> {
    @Nonnull
    private final FieldStateListener delegate;

    public FieldStateListenerWrapper(@Nonnull FieldStateListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void endpointStateChanged(StateAndValue currentState) {
        FieldState state = EndpointState.toFieldState(currentState.state);
        delegate.fieldStateChanged(state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldStateListenerWrapper that = (FieldStateListenerWrapper) o;

        return delegate.equals(that.delegate);

    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
