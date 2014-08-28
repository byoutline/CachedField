package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.FieldState;
import com.byoutline.eventcallback.internal.SessionChecker;
import javax.inject.Provider;

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedValue<T> {

    private FieldState fieldState = FieldState.NOT_LOADED;
    private T value;
    private String valueSession;
    public final Provider<String> sessionProvider;

    public CachedValue(Provider<String> sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    private void checkSession() {
        SessionChecker checker = new SessionChecker(sessionProvider, valueSession);
        if (!checker.isSameSession()) {
            fieldState = FieldState.NOT_LOADED;
            value = null;
        }
    }

    public synchronized void loadingStarted() {
        fieldState = FieldState.CURRENTLY_LOADING;
        this.valueSession = sessionProvider.get();
    }

    public synchronized void setValue(T value) {
        fieldState = FieldState.LOADED;
        this.value = value;
    }

    public synchronized void valueLoadingFailed() {
        fieldState = FieldState.NOT_LOADED;
        this.value = null;
    }

    public synchronized T getValue() {
        return value;
    }

    public synchronized StateAndValue<T> getStateAndValue() {
        checkSession();
        return new StateAndValue<T>(fieldState, value);
    }
}
