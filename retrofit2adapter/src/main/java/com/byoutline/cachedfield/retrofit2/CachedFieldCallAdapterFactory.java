package com.byoutline.cachedfield.retrofit2;


import com.byoutline.cachedfield.internal.DefaultExecutors;
import com.byoutline.cachedfield.utils.SameSessionIdProvider;
import com.byoutline.observablecachedfield.ObservableCachedField;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public final class CachedFieldCallAdapterFactory extends CallAdapter.Factory {
    private final Provider<String> sessionIdProvider;
    private final ExecutorService valueGetterExecutor;
    private final Executor stateListenerExecutor;

    CachedFieldCallAdapterFactory(Provider<String> sessionIdProvider, ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        this.sessionIdProvider = sessionIdProvider;
        this.valueGetterExecutor = valueGetterExecutor;
        this.stateListenerExecutor = stateListenerExecutor;
    }

    public static CachedFieldCallAdapterFactory create() {
        return create(new SameSessionIdProvider(),
                DefaultExecutors.createDefaultValueGetterExecutor(),
                DefaultExecutors.createDefaultStateListenerExecutor());
    }

    public static CachedFieldCallAdapterFactory create(Provider<String> sessionIdProvider) {
        return create(sessionIdProvider,
                DefaultExecutors.createDefaultValueGetterExecutor(),
                DefaultExecutors.createDefaultStateListenerExecutor());
    }

    public static CachedFieldCallAdapterFactory create(ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        return create(new SameSessionIdProvider(),
                valueGetterExecutor,
                stateListenerExecutor);
    }

    public static CachedFieldCallAdapterFactory create(Provider<String> sessionIdProvide, ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        return new CachedFieldCallAdapterFactory(sessionIdProvide, valueGetterExecutor, stateListenerExecutor);
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        boolean isObservable = rawType == ObservableCachedField.class;
        if (!isObservable) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException(" return type must be parameterized");
        }
        Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);

        return new ObservableCachedFieldCallAdapter(responseType, sessionIdProvider, valueGetterExecutor, stateListenerExecutor);
    }
}
