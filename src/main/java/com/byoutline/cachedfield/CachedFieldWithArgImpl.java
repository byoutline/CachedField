package com.byoutline.cachedfield;

import com.byoutline.cachedfield.cachedendpoint.EndpointState;
import com.byoutline.cachedfield.cachedendpoint.FieldStateListenerWrapper;
import com.byoutline.cachedfield.internal.CachedValue;
import com.byoutline.cachedfield.internal.StateAndValue;
import com.byoutline.cachedfield.internal.ValueLoader;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static com.byoutline.cachedfield.internal.DefaultExecutors.createDefaultStateListenerExecutor;
import static com.byoutline.cachedfield.internal.DefaultExecutors.createDefaultValueGetterExecutor;

/**
 * Cached field where value getter requires single argument. If argument does
 * change between calls it is assumed that value needs to be refreshed.
 *
 * @param <RETURN_TYPE> Type of value to be cached
 * @param <ARG_TYPE>    Argument needed to calculate value.
 * @author Sebastian Kacprzak <nait at naitbit.com>
 */
public class CachedFieldWithArgImpl<RETURN_TYPE, ARG_TYPE> implements CachedFieldWithArg<RETURN_TYPE, ARG_TYPE> {

    private final SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successListener;
    private final CachedValue<RETURN_TYPE, ARG_TYPE> value;
    private final ValueLoader<RETURN_TYPE, ARG_TYPE> valueLoader;

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
    public CachedFieldWithArgImpl(@Nonnull Provider<String> sessionProvider,
                                  @Nonnull ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                                  @Nonnull SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successHandler,
                                  @Nonnull ErrorListenerWithArg<ARG_TYPE> errorHandler) {
        this(sessionProvider, valueGetter, successHandler, errorHandler,
                createDefaultValueGetterExecutor(), createDefaultStateListenerExecutor());
    }

    /**
     * @param sessionProvider       Provider that returns String unique for current
     *                              session. When session changes cached value will be dropped.
     * @param valueGetter           Provider that synchronously calculates/fetches value
     *                              and returns it.
     * @param successHandler        Listener that will be informed when value is
     *                              successfully calculated.
     * @param errorHandler          Listener that will be be informed when calculation of
     *                              value fails.
     * @param valueGetterExecutor   ExecutorService that will be used to call valueGetter and call
     * @param stateListenerExecutor
     */
    public CachedFieldWithArgImpl(@Nonnull Provider<String> sessionProvider,
                                  @Nonnull ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                                  @Nonnull SuccessListenerWithArg<RETURN_TYPE, ARG_TYPE> successHandler,
                                  @Nonnull ErrorListenerWithArg<ARG_TYPE> errorHandler,
                                  @Nonnull ExecutorService valueGetterExecutor,
                                  @Nonnull Executor stateListenerExecutor) {
        this.value = new CachedValue<RETURN_TYPE, ARG_TYPE>(sessionProvider, stateListenerExecutor);
        this.valueLoader = new ValueLoader<RETURN_TYPE, ARG_TYPE>(valueGetter,
                successHandler, errorHandler, value, valueGetterExecutor, true);
        this.successListener = successHandler;
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
        if (argChanged) {
            refresh(arg);
            return;
        }
        StateAndValue<RETURN_TYPE, ARG_TYPE> stateAndValue = value.getStateAndValue();
        FieldState state = EndpointState.toFieldState(stateAndValue.state);
        switch (state) {
            case NOT_LOADED:
                refresh(arg);
                break;
            case CURRENTLY_LOADING:
                // Event will be posted when value is fully loaded.
                break;
            case LOADED:
                successListener.valueLoaded(stateAndValue.value.getSuccessResult(), stateAndValue.arg);
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
    private void loadValue(final ARG_TYPE arg) {
        valueLoader.loadValue(arg);
    }

    @Override
    public FieldState getState() {
        return EndpointState.toFieldState(value.getStateAndValue().state);
    }

    @Override
    public void drop() {
        value.drop();
    }

    @Override
    public void addStateListener(@Nonnull FieldStateListener listener) throws IllegalArgumentException {
        value.addStateListener(new FieldStateListenerWrapper<RETURN_TYPE, ARG_TYPE>(listener));
    }

    @Override
    public boolean removeStateListener(@Nonnull FieldStateListener listener) throws IllegalArgumentException {
        return value.removeStateListener(new FieldStateListenerWrapper<RETURN_TYPE, ARG_TYPE>(listener));
    }
}
