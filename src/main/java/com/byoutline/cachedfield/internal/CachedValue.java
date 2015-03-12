package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.FieldState;
import com.byoutline.cachedfield.FieldStateListener;
import com.byoutline.eventcallback.internal.SessionChecker;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Provider;

/**
 * Thread safe value storage, that nulls out its content when session changes.
 *
 * @param <VALUE_TYPE> Type of stored value
 * @param <ARG_TYPE> Type of argument needed to calculate value
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedValue<VALUE_TYPE, ARG_TYPE> {

    private FieldState fieldState = FieldState.NOT_LOADED;
    private VALUE_TYPE value;
    private ARG_TYPE arg;
    private String valueSession;
    private final List<FieldStateListener> fieldStateListeners = new ArrayList<FieldStateListener>(2);
    public final Provider<String> sessionProvider;

    public CachedValue(@Nonnull Provider<String> sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    private void checkSession() {
        SessionChecker checker = new SessionChecker(sessionProvider, valueSession);
        if (!checker.isSameSession()) {
            drop();
        }
    }

    public synchronized void loadingStarted() {
        setState(FieldState.CURRENTLY_LOADING);
        this.valueSession = sessionProvider.get();
    }

    public synchronized void setValue(VALUE_TYPE value, ARG_TYPE arg) {
        setState(FieldState.LOADED);
        this.value = value;
        this.arg = arg;
    }

    public synchronized void valueLoadingFailed() {
        drop();
    }
    
    public synchronized void drop() {
        setState(FieldState.NOT_LOADED);
        value = null;
        arg = null;
    }

    public synchronized StateAndValue<VALUE_TYPE, ARG_TYPE> getStateAndValue() {
        checkSession();
        return new StateAndValue<VALUE_TYPE, ARG_TYPE>(fieldState, value, arg);
    }

    private void setState(FieldState newState) {
        if (newState == fieldState) {
            return;
        }
        fieldState = newState;
        for (FieldStateListener fieldStateListener : fieldStateListeners) {
            fieldStateListener.fieldStateChanged(newState);
        }
    }

    /**
     * Register listener that will be informed each time {@link FieldState}
     * changes.
     *
     * @param listener
     * @throws IllegalArgumentException if listener is null
     */
    public synchronized void addStateListener(@Nonnull FieldStateListener listener) throws IllegalArgumentException {
        checkListenerNonNull(listener);
        fieldStateListeners.add(listener);
    }

    /**
     * Remove field state listener
     *
     * @param listener
     * @return true if listeners collection was modified by this operation,
     * false otherwise
     * @throws IllegalArgumentException if listener is null
     */
    public synchronized boolean removeStateListener(@Nonnull FieldStateListener listener) throws IllegalArgumentException {
        checkListenerNonNull(listener);
        return fieldStateListeners.remove(listener);
    }

    private void checkListenerNonNull(FieldStateListener listener) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
    }

    public ARG_TYPE getArg() {
        return arg;
    }
}
