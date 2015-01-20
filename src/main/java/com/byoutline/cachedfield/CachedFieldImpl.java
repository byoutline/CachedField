package com.byoutline.cachedfield;

import com.byoutline.cachedfield.internal.StubErrorListener;
import com.byoutline.cachedfield.internal.CachedValue;
import com.byoutline.cachedfield.internal.StubFieldStateListener;
import javax.annotation.Nonnull;
import javax.inject.Provider;

/**
 * Default implementation of {@link CachedField}. Loads value on separate thread
 * and informs listeners on success and error.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 * @param <T> Type of value to be cached
 */
public class CachedFieldImpl<T> implements CachedField<T> {

    private final Provider<T> valueGetter;
    private final SuccessListener<T> successListener;
    private final ErrorListener errorListener;
    private final CachedValue<T> value;

    /**
     * Constructor for situation that we are not interested in failures, only in
     * successfully calculated values.
     *
     * @param sessionProvider Provider that returns String unique for current
     * session. When session changes cached value will be dropped.
     * @param valueGetter Provider that synchronously calculates/fetches value
     * and returns it.
     * @param successListener Listener that will be informed when value is
     * successfully calculated.
     */
    public CachedFieldImpl(@Nonnull Provider<String> sessionProvider,
            @Nonnull Provider<T> valueGetter,
            @Nonnull SuccessListener<T> successListener) {
        this(sessionProvider, valueGetter, successListener, new StubErrorListener());
    }

    /**
     *
     * @param sessionProvider Provider that returns String unique for current
     * session. When session changes cached value will be dropped.
     * @param valueGetter Provider that synchronously calculates/fetches value
     * and returns it.
     * @param successHandler Listener that will be informed when value is
     * successfully calculated.
     * @param errorHandler Listener that will be be informed when calculation
     * of value fails.
     */
    public CachedFieldImpl(@Nonnull Provider<String> sessionProvider,
            @Nonnull Provider<T> valueGetter,
            @Nonnull SuccessListener<T> successHandler,
            @Nonnull ErrorListener errorHandler) {
        this(sessionProvider, valueGetter, successHandler,
                errorHandler, new StubFieldStateListener());
    }

    /**
     *
     * @param sessionProvider Provider that returns String unique for current
     * session. When session changes cached value will be dropped.
     * @param valueGetter Provider that synchronously calculates/fetches value
     * and returns it.
     * @param successHandler Listener that will be informed when value is
     * successfully calculated.
     * @param errorHandler Listener that will be be informed when calculation
     * of value fails.
     * @param fieldStateListener Listener that will be informed when field 
     * changes its state (fe: from NOT_LOADED to LOADING)
     */
    public CachedFieldImpl(@Nonnull Provider<String> sessionProvider,
            @Nonnull Provider<T> valueGetter,
            @Nonnull SuccessListener<T> successHandler,
            @Nonnull ErrorListener errorHandler,
            @Nonnull FieldStateListener fieldStateListener) {
        this.value = new CachedValue<T>(sessionProvider, fieldStateListener);
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
                    T fetchedValue = valueGetter.get();
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
}
