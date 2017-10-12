package com.byoutline.cachedfield;

import com.byoutline.cachedfield.internal.DefaultExecutors;
import com.byoutline.cachedfield.internal.StubErrorListener;
import com.byoutline.cachedfield.internal.VoidArgumentFactory;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Default implementation of {@link CachedField}. Loads value on separate thread
 * and informs listeners on success and error.
 *
 * @param <RETURN_TYPE> Type of value to be cached
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedFieldImpl<RETURN_TYPE> implements CachedField<RETURN_TYPE> {

    private final CachedFieldWithArgImpl<RETURN_TYPE, Void> delegate;

    /**
     * Constructor for situation that we are not interested in failures, only in
     * successfully calculated values.
     *
     * @param sessionProvider Provider that returns String unique for current
     *                        session. When session changes cached value will be dropped.
     * @param valueGetter     Provider that synchronously calculates/fetches value
     *                        and returns it.
     * @param successListener Listener that will be informed when value is
     *                        successfully calculated.
     */
    public CachedFieldImpl(@Nonnull Provider<String> sessionProvider,
                           @Nonnull Provider<RETURN_TYPE> valueGetter,
                           @Nonnull SuccessListener<RETURN_TYPE> successListener) {
        this(sessionProvider, valueGetter, successListener, new StubErrorListener());
    }

    /**
     * @param sessionProvider Provider that returns String unique for current
     *                        session. When session changes cached value will be dropped.
     * @param valueGetter     Provider that synchronously calculates/fetches value
     *                        and returns it.
     * @param successHandler  Listener that will be informed when value is
     *                        successfully calculated.
     * @param errorHandler    Listener that will be be informed when calculation of
     *                        value fails.
     */
    public CachedFieldImpl(@Nonnull Provider<String> sessionProvider,
                           @Nonnull Provider<RETURN_TYPE> valueGetter,
                           @Nonnull SuccessListener<RETURN_TYPE> successHandler,
                           @Nonnull ErrorListener errorHandler) {
        this(sessionProvider, valueGetter, successHandler, errorHandler,
                DefaultExecutors.createDefaultValueGetterExecutor(),
                DefaultExecutors.createDefaultStateListenerExecutor());
    }

    public CachedFieldImpl(@Nonnull Provider<String> sessionProvider,
                           @Nonnull Provider<RETURN_TYPE> valueGetter,
                           @Nonnull SuccessListener<RETURN_TYPE> successHandler,
                           @Nonnull ErrorListener errorHandler,
                           @Nonnull ExecutorService valueGetterExecutor,
                           @Nonnull Executor stateListenerExecutor) {
        ProviderWithArg<RETURN_TYPE, Void> valueGetterWithArg = VoidArgumentFactory.addVoidArg(valueGetter);
        SuccessListenerWithArg<RETURN_TYPE, Void> success = VoidArgumentFactory.addVoidArg(successHandler);
        ErrorListenerWithArg<Void> error = VoidArgumentFactory.addVoidArg(errorHandler);
        delegate = new CachedFieldWithArgImpl<RETURN_TYPE, Void>(sessionProvider,
                valueGetterWithArg, success, error, valueGetterExecutor, stateListenerExecutor);
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
    public void addStateListener(@Nonnull FieldStateListener listener) throws IllegalArgumentException {
        delegate.addStateListener(listener);
    }

    @Override
    public boolean removeStateListener(@Nonnull FieldStateListener listener) throws IllegalArgumentException {
        return delegate.removeStateListener(listener);
    }

    @Override
    public CachedFieldWithArgImpl<RETURN_TYPE, Void> toCachedFieldWithArg() {
        return delegate;
    }
}
