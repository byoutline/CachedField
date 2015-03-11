package com.byoutline.cachedfield;

import com.byoutline.cachedfield.internal.CachedValue;
import com.byoutline.cachedfield.internal.StubErrorListener;

import javax.annotation.Nonnull;
import javax.inject.Provider;

/**
 * Default implementation of {@link CachedField}. Loads value on separate thread
 * and informs listeners on success and error.
 *
 * @param <RETURN_TYPE> Type of value to be cached
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedFieldImpl<RETURN_TYPE> implements CachedField<RETURN_TYPE> {

    private final Provider<RETURN_TYPE> valueGetter;
    private final SuccessListener<RETURN_TYPE> successListener;
    private final ErrorListener errorListener;
    private final CachedValue<RETURN_TYPE> value;

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
     * @param errorHandler    Listener that will be be informed when calculation
     *                        of value fails.
     */
    public CachedFieldImpl(@Nonnull Provider<String> sessionProvider,
                           @Nonnull Provider<RETURN_TYPE> valueGetter,
                           @Nonnull SuccessListener<RETURN_TYPE> successHandler,
                           @Nonnull ErrorListener errorHandler) {
        this.value = new CachedValue<RETURN_TYPE>(sessionProvider);
        this.valueGetter = valueGetter;
        this.successListener = successHandler;
        this.errorListener = errorHandler;
    }

    @Override
    public FieldState getState() {
        return value.getStateAndValue().state;
    }

    @Override
    public void postValue() {
        switch (getState()) {
            case NOT_LOADED:
                refresh();
                break;
            case CURRENTLY_LOADING:
                // Event will be posted when value is fully loaded.
                break;
            case LOADED:
                successListener.valueLoaded(value.getValue());
                break;
        }
    }

    @Override
    public void refresh() {
        loadValue();
    }

    /**
     * Loads value in separate thread.
     */
    private void loadValue() {
        Thread fetchThread = new Thread() {

            @Override
            public void run() {
                try {
                    value.loadingStarted();
                    RETURN_TYPE fetchedValue = valueGetter.get();
                    value.setValue(fetchedValue);
                    successListener.valueLoaded(fetchedValue);
                } catch (Exception ex) {
                    value.valueLoadingFailed();
                    errorListener.valueLoadingFailed(ex);
                }
            }
        };
        fetchThread.start();
    }

    @Override
    public void drop() {
        value.drop();
    }

    @Override
    public void addStateListener(@Nonnull FieldStateListener listener) throws IllegalArgumentException {
        value.addStateListener(listener);
    }

    @Override
    public boolean removeStateListener(@Nonnull FieldStateListener listener) throws IllegalArgumentException {
        return value.removeStateListener(listener);
    }
}
