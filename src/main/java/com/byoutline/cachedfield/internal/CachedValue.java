package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.FieldStateListener;
import com.byoutline.cachedfield.FieldState;
import com.byoutline.eventcallback.internal.SessionChecker;
import javax.annotation.Nonnull;
import javax.inject.Provider;

/**
 * Thread safe value storage, that nulls out its content when session changes.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 * @param <T> Type of stored value
 */
public class CachedValue<T> {

    private FieldState fieldState = FieldState.NOT_LOADED;
    private T value;
    private String valueSession;
    private final FieldStateListener fieldStateListener;
    public final Provider<String> sessionProvider;

    public CachedValue(@Nonnull Provider<String> sessionProvider, 
            @Nonnull FieldStateListener fieldStateListener) {
        this.sessionProvider = sessionProvider;
        this.fieldStateListener = fieldStateListener;
    }

    private void checkSession() {
        SessionChecker checker = new SessionChecker(sessionProvider, valueSession);
        if (!checker.isSameSession()) {
            setState(FieldState.NOT_LOADED);
            value = null;
        }
    }

    public synchronized void loadingStarted() {
        setState(FieldState.CURRENTLY_LOADING);
        this.valueSession = sessionProvider.get();
    }

    public synchronized void setValue(T value) {
        setState(FieldState.LOADED);
        this.value = value;
    }

    public synchronized void valueLoadingFailed() {
        drop();
    }

    public synchronized T getValue() {
        return value;
    }

    public synchronized void drop() {
        setState(FieldState.NOT_LOADED);
        this.value = null;
    }

    public synchronized StateAndValue<T> getStateAndValue() {
        checkSession();
        return new StateAndValue<T>(fieldState, value);
    }
    
    private void setState(FieldState newState) {
        if(newState == fieldState) {
            return;
        }
        fieldState = newState;
        fieldStateListener.fieldStateChanged(newState);
    }
}
