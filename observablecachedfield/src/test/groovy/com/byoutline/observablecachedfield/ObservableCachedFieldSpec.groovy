package com.byoutline.observablecachedfield

import com.byoutline.cachedfield.ErrorListener
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.SuccessListener
import com.byoutline.cachedfield.testsuite.CachedFieldCommonSuiteSpec

import javax.inject.Provider
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

class ObservableCachedFieldSpec extends CachedFieldCommonSuiteSpec {
    @Override
    def getField(Provider<String> valueProvider, ExecutorService valueGetterExecutor, Executor stateListenerExecutor) {
        ObservableCachedField<String> field = new ObservableCachedField<String>(MockFactory.getSameSessionIdProvider(),
                valueProvider,
                {} as SuccessListener<String>,
                {} as ErrorListener,
                valueGetterExecutor,
                stateListenerExecutor
        )
        return field
    }
}