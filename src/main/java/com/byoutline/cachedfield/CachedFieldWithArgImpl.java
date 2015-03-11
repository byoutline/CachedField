package com.byoutline.cachedfield;

import com.byoutline.cachedfield.internal.CachedValue;
import com.byoutline.cachedfield.internal.StateAndValue;

import javax.annotation.Nonnull;
import javax.inject.Provider;

/**
 * Cached field where value getter requires single argument. If argument does
 * change between calls it is assumed that value needs to be refreshed.
 *
 * @author Sebastian Kacprzak <nait at naitbit.com>
 * @param <RETURN_TYPE> Type of value to be cached
 * @param <ARG_TYPE> Argument needed to calculate value.
 */
public class CachedFieldWithArgImpl<RETURN_TYPE, ARG_TYPE> implements CachedFieldWithArg<RETURN_TYPE, ARG_TYPE> {

    private final ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter;
    private final SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successListener;
    private final ErrorListenerWithArg<ARG_TYPE> errorListener;
    private final CachedValue<RETURN_TYPE, ARG_TYPE> value;

    /**
     * @param sessionProvider Provider that returns String unique for current
     * session. When session changes cached value will be dropped.
     * @param valueGetter Provider that synchronously calculates/fetches value
     * and returns it.
     * @param successHandler Listener that will be informed when value is
     * successfully calculated.
     * @param errorHandler Listener that will be be informed when calculation of
     * value fails.
     */
    public CachedFieldWithArgImpl(@Nonnull Provider<String> sessionProvider,
            @Nonnull ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
            @Nonnull SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successHandler,
            @Nonnull ErrorListenerWithArg<ARG_TYPE> errorHandler) {
        this.value = new CachedValue<RETURN_TYPE, ARG_TYPE>(sessionProvider);
        this.valueGetter = valueGetter;
        this.successListener = successHandler;
        this.errorListener = errorHandler;
    }

    private boolean argChanged(ARG_TYPE newArg) {
        ARG_TYPE oldArg = value.getArg();
        if (newArg == null) {
            return oldArg != null;
        }
        return !newArg.equals(oldArg);
    }

    @Override
    public void postValue(ARG_TYPE arg) {
        boolean argChanged = argChanged(arg);
        if(argChanged) {
            refresh(arg);
            return;
        }
        StateAndValue<RETURN_TYPE, ARG_TYPE> stateAndValue = value.getStateAndValue();
        switch (stateAndValue.state) {
            case NOT_LOADED:
                refresh(arg);
                break;
            case CURRENTLY_LOADING:
                // Event will be posted when value is fully loaded.
                break;
            case LOADED:
                successListener.valueLoaded(stateAndValue.value, stateAndValue.arg);
                break;
        }
    }

    @Override
    public void refresh(ARG_TYPE arg) {
        loadValue(arg);
    }
    
    /**
     * Loads value in separate thread.
     */
    private void loadValue(ARG_TYPE arg) {
        Thread fetchThread = new Thread() {

            @Override
            public void run() {
                try {
                    value.loadingStarted();
                    RETURN_TYPE fetchedValue = valueGetter.get(arg);
                    value.setValue(fetchedValue, arg);
                    successListener.valueLoaded(fetchedValue, arg);
                } catch (Exception ex) {
                    value.valueLoadingFailed();
                    errorListener.valueLoadingFailed(ex, arg);
                }
            }
        };
        fetchThread.start();
    }

    @Override
    public FieldState getState() {
        return value.getStateAndValue().state;
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
