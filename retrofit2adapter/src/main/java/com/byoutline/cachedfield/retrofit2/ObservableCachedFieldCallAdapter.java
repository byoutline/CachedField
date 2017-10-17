package com.byoutline.cachedfield.retrofit2;

import com.byoutline.ottocachedfield.CachedFieldBuilder;
import retrofit2.Call;
import retrofit2.CallAdapter;

import javax.inject.Provider;

import static com.byoutline.ibuscachedfield.util.RetrofitHelper.apiValueProv;

import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

final class ObservableCachedFieldCallAdapter<R> implements CallAdapter<R, Object> {
    private final Type responseType;
    private final Provider<String> sessionIdProvider;
    private final ExecutorService valueGetterExecutor;
    private final Executor stateListenerExecutor;

    ObservableCachedFieldCallAdapter(Type responseType, Provider<String> sessionIdProvider,
                                     ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        this.responseType = responseType;
        this.sessionIdProvider = sessionIdProvider;
        this.valueGetterExecutor = valueGetterExecutor;
        this.stateListenerExecutor = stateListenerExecutor;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public Object adapt(final Call<R> call) {
        return new CachedFieldBuilder()
                .withValueProvider(apiValueProv(new Provider<Call<R>>() {
                    @Override
                    public Call<R> get() {
                        return call;
                    }
                })).asObservable()
                .withCustomSessionIdProvider(sessionIdProvider)
                .withCustomStateListenerExecutor(stateListenerExecutor)
                .withCustomValueGetterExecutor(valueGetterExecutor)
                .build();
    }
}
