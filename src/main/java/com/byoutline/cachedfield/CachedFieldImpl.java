package com.byoutline.cachedfield;

import com.byoutline.cachedfield.internal.StubErrorListener;
import com.byoutline.cachedfield.internal.CachedValue;
import javax.inject.Provider;

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedFieldImpl<T> implements CachedField {

    private final Provider<T> valueGetter;
    private final SuccessListener<T> successListener;
    private final ErrorListener errorListener;
    private final CachedValue<T> value;

    public CachedFieldImpl(Provider<String> sessionProvider,
            Provider<T> valueGetter,
            SuccessListener<T> successListener) {
        this(sessionProvider, valueGetter, successListener, new StubErrorListener());
    }

    public CachedFieldImpl(Provider<String> sessionProvider,
            Provider<T> valueGetter,
            SuccessListener<T> successHandler, ErrorListener errorHandler) {
        this.value = new CachedValue<T>(sessionProvider);
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
}
