package com.byoutline.cachedfield;

import com.byoutline.eventcallback.EventCallback;
import com.byoutline.eventcallback.IBus;
import com.squareup.otto.Bus;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Provider;

/**
 *
 * @author Sebastian Kacprzak <nait at naitbit.com>
 */
public class CachedFieldImpl<T> implements CachedField {

    private final Bus bus;
    private final Provider<String> sessionProvider;
    private final FieldGetter<T> valueGetter;
    private T value;
    private AtomicBoolean workInProgress = new AtomicBoolean(false);
    private FieldState state = FieldState.NOT_LOADED;

    public CachedFieldImpl(Bus bus, Provider<String> sessionProvider, FieldGetter<T> valueGetter) {
        this.bus = bus;
        this.sessionProvider = sessionProvider;
        this.valueGetter = valueGetter;
        bus.register(this);
    }

    @Override
    public FieldState getState() {
        return state;
    }

    @Override
    public void postValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void refresh() {
        Callback<T> cb = EventCallback.builder(null, null)
        valueGetter.get();
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private void loadValue() {
        
    }
    

}
