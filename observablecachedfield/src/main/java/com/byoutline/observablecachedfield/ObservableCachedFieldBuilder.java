package com.byoutline.observablecachedfield;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.internal.DefaultExecutors;
import com.byoutline.cachedfield.internal.StubErrorListener;
import com.byoutline.cachedfield.utils.SameSessionIdProvider;
import com.byoutline.observablecachedfield.internal.NullArgumentException;
import com.byoutline.observablecachedfield.internal.StubErrorListenerWithArg;
import com.byoutline.observablecachedfield.internal.StubSuccessListener;
import com.byoutline.observablecachedfield.internal.StubSuccessListenerWithArg;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class ObservableCachedFieldBuilder {

    private static Provider<String> defaultSessionIdProvider = new SameSessionIdProvider();
    private static ExecutorService defaultValueGetterExecutor = DefaultExecutors.createDefaultValueGetterExecutor();
    private static Executor defaultStateListenerExecutor = DefaultExecutors.createDefaultStateListenerExecutor();

    public static void setDefaultSessionIdProvider(Provider<String> defaultSessionIdProvider) {
        ObservableCachedFieldBuilder.defaultSessionIdProvider = defaultSessionIdProvider;
    }

    public static void setDefaultStateListenerExecutor(Executor defaultStateListenerExecutor) {
        ObservableCachedFieldBuilder.defaultStateListenerExecutor = defaultStateListenerExecutor;
    }

    public static void setDefaultValueGetterExecutor(ExecutorService defaultValueGetterExecutor) {
        ObservableCachedFieldBuilder.defaultValueGetterExecutor = defaultValueGetterExecutor;
    }

    public <RETURN_TYPE> AsObservableNoArg<RETURN_TYPE> withValueProvider(@Nonnull Provider<RETURN_TYPE> provider) {
        validateNotNull(provider);
        return new AsObservableNoArg<>(provider);
    }

    public <RETURN_TYPE, ARG_TYPE> AsObservableWithArg<RETURN_TYPE, ARG_TYPE> withValueProviderWithArg(@Nonnull ProviderWithArg<RETURN_TYPE, ARG_TYPE> provider) {
        validateNotNull(provider);
        return new AsObservableWithArg<>(provider);
    }

    public static class AsObservableNoArg<RETURN_TYPE> {
        final Provider<RETURN_TYPE> provider;

        protected AsObservableNoArg(Provider<RETURN_TYPE> provider) {
            this.provider = provider;
        }

        public OverrideDefaultsSetter<ObservableCachedField<RETURN_TYPE>> withCustomSessionIdProvider(@Nonnull Provider<String> customSessionIdProvider) {
            return builder().withCustomSessionIdProvider(customSessionIdProvider);
        }

        public OverrideDefaultsSetter<ObservableCachedField<RETURN_TYPE>> withCustomValueGetterExecutor(@Nonnull ExecutorService valueGetterExecutor) {
            return builder().withCustomValueGetterExecutor(valueGetterExecutor);
        }

        public OverrideDefaultsSetter<ObservableCachedField<RETURN_TYPE>> withCustomStateListenerExecutor(@Nonnull Executor stateListenerExecutor) {
            return builder().withCustomStateListenerExecutor(stateListenerExecutor);
        }

        private OverrideDefaultsSetter<ObservableCachedField<RETURN_TYPE>> builder() {
            return new OverrideDefaultsSetter<>(new ConstructorWrapper<ObservableCachedField<RETURN_TYPE>>() {
                @Override
                public ObservableCachedField<RETURN_TYPE> build(Provider<String> sessionIdProvider, ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
                    return new ObservableCachedField<>(
                            sessionIdProvider,
                            provider,
                            new StubSuccessListener<RETURN_TYPE>(),
                            new StubErrorListener(),
                            valueGetterExecutor,
                            stateListenerExecutor
                    );
                }
            });
        }

        public ObservableCachedField<RETURN_TYPE> build() {
            return builder().build();
        }
    }

    public static class AsObservableWithArg<RETURN_TYPE, ARG_TYPE> {
        final ProviderWithArg<RETURN_TYPE, ARG_TYPE> provider;

        public AsObservableWithArg(ProviderWithArg<RETURN_TYPE, ARG_TYPE> provider) {
            this.provider = provider;
        }

        public OverrideDefaultsSetter<ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>> withCustomSessionIdProvider(@Nonnull Provider<String> customSessionIdProvider) {
            return builder().withCustomSessionIdProvider(customSessionIdProvider);
        }

        public OverrideDefaultsSetter<ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>> withCustomValueGetterExecutor(@Nonnull ExecutorService valueGetterExecutor) {
            return builder().withCustomValueGetterExecutor(valueGetterExecutor);
        }

        public OverrideDefaultsSetter<ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>> withCustomStateListenerExecutor(@Nonnull Executor stateListenerExecutor) {
            return builder().withCustomStateListenerExecutor(stateListenerExecutor);
        }

        private OverrideDefaultsSetter<ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>> builder() {
            return new OverrideDefaultsSetter<>(new ConstructorWrapper<ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>>() {
                @Override
                public ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE> build(Provider<String> sessionIdProvider, ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
                    return new ObservableCachedFieldWithArg<>(
                            sessionIdProvider,
                            provider,
                            new StubSuccessListenerWithArg<RETURN_TYPE, ARG_TYPE>(),
                            new StubErrorListenerWithArg<ARG_TYPE>(),
                            valueGetterExecutor,
                            stateListenerExecutor
                    );
                }
            });
        }

        public ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE> build() {
            return builder().build();
        }
    }

    public static class OverrideDefaultsSetter<FIELD_BUILD_TYPE> {
        private final ConstructorWrapper<FIELD_BUILD_TYPE> constructorWrapper;
        private Provider<String> sessionIdProvider;
        private ExecutorService valueGetterExecutor;
        private Executor stateListenerExecutor;

        protected OverrideDefaultsSetter(ConstructorWrapper<FIELD_BUILD_TYPE> constructorWrapper) {
            this.constructorWrapper = constructorWrapper;
            sessionIdProvider = defaultSessionIdProvider;
            valueGetterExecutor = defaultValueGetterExecutor;
            stateListenerExecutor = defaultStateListenerExecutor;
        }

        public OverrideDefaultsSetter<FIELD_BUILD_TYPE> withCustomSessionIdProvider(@Nonnull Provider<String> sessionIdProvider) {
            OverrideDefaultsSetter.this.sessionIdProvider = sessionIdProvider;
            return this;
        }

        public OverrideDefaultsSetter<FIELD_BUILD_TYPE> withCustomValueGetterExecutor(@Nonnull ExecutorService valueGetterExecutor) {
            OverrideDefaultsSetter.this.valueGetterExecutor = valueGetterExecutor;
            return this;
        }

        public OverrideDefaultsSetter<FIELD_BUILD_TYPE> withCustomStateListenerExecutor(@Nonnull Executor stateListenerExecutor) {
            OverrideDefaultsSetter.this.stateListenerExecutor = stateListenerExecutor;
            return this;
        }

        public FIELD_BUILD_TYPE build() {
            validateNotNull(valueGetterExecutor, stateListenerExecutor);
            return constructorWrapper.build(sessionIdProvider, valueGetterExecutor, stateListenerExecutor);
        }
    }

    private static void validateNotNull(Object... values) throws NullArgumentException {
        for (Object value : values) {
            if (value == null) {
                throw new NullArgumentException();
            }
        }
    }

    protected interface ConstructorWrapper<FIELD_BUILD_TYPE> {
        @Nonnull
        FIELD_BUILD_TYPE build(Provider<String> sessionIdProvider, ExecutorService valueGetterExecutor, Executor stateListenerExecutor);
    }
}
