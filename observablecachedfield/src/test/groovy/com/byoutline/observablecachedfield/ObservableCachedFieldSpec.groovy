package com.byoutline.observablecachedfield

import com.byoutline.cachedfield.testsuite.CachedFieldCommonSuiteSpec

import javax.inject.Provider
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

class ObservableCachedFieldSpec extends CachedFieldCommonSuiteSpec {
    @Override
    def getField(Provider<String> valueProvider, ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        ObservableCachedField<String> field = new ObservableCachedFieldBuilder()
                .withValueProvider(valueProvider)
                .withCustomValueGetterExecutor(valueGetterExecutor)
                .withCustomStateListenerExecutor(stateListenerExecutor)
                .build()
        return field
    }
}