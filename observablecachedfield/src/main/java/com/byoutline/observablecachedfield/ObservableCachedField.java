package com.byoutline.observablecachedfield;

import android.databinding.ObservableField;
import com.byoutline.cachedfield.*;
import com.byoutline.cachedfield.internal.VoidArgumentFactory;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * No arg version of {@link ObservableCachedFieldWithArg}
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class ObservableCachedField<RETURN_TYPE> implements CachedField<RETURN_TYPE> {
    private final ObservableCachedFieldWithArg<RETURN_TYPE, Void> delegate;

    public ObservableCachedField(Provider<String> sessionIdProvider,
                                    Provider<RETURN_TYPE> valueGetter,
                                    SuccessListener<RETURN_TYPE> additionalSuccessListener,
                                    ErrorListener additionalErrorListener,
                                    ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        delegate = new ObservableCachedFieldWithArg<RETURN_TYPE, Void>(
                sessionIdProvider,
                VoidArgumentFactory.addVoidArg(valueGetter),
                VoidArgumentFactory.addVoidArg(additionalSuccessListener),
                VoidArgumentFactory.addVoidArg(additionalErrorListener),
                valueGetterExecutor, stateListenerExecutor
        );
    }

    public ObservableField<RETURN_TYPE> observable() {
        return delegate.observable();
    }

    @Override
    public FieldState getState() {
        return delegate.getState();
    }

    @Override
    public void postValue() {
        delegate.postValue(null);
    }

    @Override
    public void refresh() {
        delegate.refresh(null);
    }

    @Override
    public void drop() {
        delegate.drop();
    }

    @Override
    public void addStateListener(FieldStateListener listener) {
        delegate.addStateListener(listener);
    }

    @Override
    public boolean removeStateListener(FieldStateListener listener) {
        return delegate.removeStateListener(listener);
    }
}
