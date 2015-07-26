package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.FieldState;
import com.byoutline.cachedfield.cachedendpoint.CallResult;
import com.byoutline.cachedfield.cachedendpoint.EndpointState;
import com.byoutline.cachedfield.cachedendpoint.EndpointStateListener;
import com.byoutline.eventcallback.internal.SessionChecker;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Thread safe value storage, that nulls out its content when session changes.
 *
 * @param <VALUE_TYPE> Type of stored value
 * @param <ARG_TYPE>   Type of argument needed to calculate value
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CachedValue<VALUE_TYPE, ARG_TYPE> {

    private EndpointState fieldState = EndpointState.BEFORE_CALL;
    private VALUE_TYPE successValue;
    private Exception errorValue;
    private ARG_TYPE arg;
    private String valueSession;
    private final List<EndpointStateListener> fieldStateListeners = new ArrayList<EndpointStateListener>(2);
    private final Provider<String> sessionProvider;
    private final Executor stateListenerExecutor;

    public CachedValue(@Nonnull Provider<String> sessionProvider,
                       @Nonnull Executor stateListenerExecutor) {
        this.sessionProvider = sessionProvider;
        this.stateListenerExecutor = stateListenerExecutor;
    }

    private void checkSession() {
        SessionChecker checker = new SessionChecker(sessionProvider, valueSession);
        if (!checker.isSameSession()) {
            drop();
        }
    }

    public synchronized void loadingStarted() {
        setState(EndpointState.DURING_CALL);
        this.valueSession = sessionProvider.get();
    }

    public synchronized void setSuccess(VALUE_TYPE value, ARG_TYPE arg) {
        this.successValue = value;
        this.errorValue = null;
        this.arg = arg;
        setState(EndpointState.CALL_SUCCESS);
    }

    public synchronized void setFailure(Exception reason, ARG_TYPE arg) {
        this.successValue = null;
        this.errorValue = reason;
        this.arg = arg;
        setState(EndpointState.CALL_FAILED);
    }

    public synchronized void valueLoadingFailed() {
        drop();
    }

    public synchronized void drop() {
        successValue = null;
        errorValue = null;
        arg = null;
        setState(EndpointState.BEFORE_CALL);
    }

    public synchronized StateAndValue<VALUE_TYPE, ARG_TYPE> getStateAndValue() {
        checkSession();
        return new StateAndValue<VALUE_TYPE, ARG_TYPE>(fieldState, new CallResult<VALUE_TYPE>(successValue, errorValue), arg);
    }

    private void setState(EndpointState newState) {
        if (newState == fieldState) {
            return;
        }
        fieldState = newState;
        informStateListeners(newState);
    }

    private void informStateListeners(final EndpointState newState) {
        stateListenerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                informStateListenersSync(newState);
            }
        });
    }

    private void informStateListenersSync(EndpointState newState) {
        // Iterate over copy of listeners to guard against listeners modification.
        List<EndpointStateListener> stateListeners = new ArrayList<EndpointStateListener>(fieldStateListeners);
        for (EndpointStateListener fieldStateListener : stateListeners) {
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
    public synchronized void addStateListener(@Nonnull EndpointStateListener listener) throws IllegalArgumentException {
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
    public synchronized boolean removeStateListener(@Nonnull EndpointStateListener listener) throws IllegalArgumentException {
        checkListenerNonNull(listener);
        return fieldStateListeners.remove(listener);
    }

    private void checkListenerNonNull(EndpointStateListener listener) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
    }

    public ARG_TYPE getArg() {
        return arg;
    }
}
