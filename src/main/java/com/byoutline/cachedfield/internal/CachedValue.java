package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.FieldState;
import com.byoutline.cachedfield.FieldStateListener;
import com.byoutline.eventcallback.internal.SessionChecker;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread safe value storage, that nulls out its content when session changes.
 *
 * @param <VALUE_TYPE> Type of stored value
 * @param <ARG_TYPE>   Type of argument needed to calculate value
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedValue<VALUE_TYPE, ARG_TYPE> {

    private FieldState fieldState = FieldState.NOT_LOADED;
    private VALUE_TYPE value;
    private ARG_TYPE arg;
    private String valueSession;
    private final List<FieldStateListener> fieldStateListeners = new ArrayList<FieldStateListener>(2);
    private final Provider<String> sessionProvider;

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
        this.value = value;
        this.arg = arg;
        setState(FieldState.LOADED);
    }

    public synchronized void valueLoadingFailed() {
        drop();
    }

    public synchronized void drop() {
        value = null;
        arg = null;
        setState(FieldState.NOT_LOADED);
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
        informStateListeners(newState);
    }

    private void informStateListeners(FieldState newState) {
        // Iterate over copy of listeners to guard against listeners modification.
        List<FieldStateListener> stateListeners = new ArrayList<FieldStateListener>(fieldStateListeners);
        for (FieldStateListener fieldStateListener : stateListeners) {
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
