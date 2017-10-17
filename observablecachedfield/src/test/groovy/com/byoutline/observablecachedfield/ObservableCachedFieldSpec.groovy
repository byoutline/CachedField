package com.byoutline.observablecachedfield

import com.byoutline.cachedfield.ErrorListener
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.SuccessListener
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.byoutline.cachedfield.testsuite.CachedFieldCommonSuiteSpec

import javax.inject.Provider

class ObservableCachedFieldSpec extends CachedFieldCommonSuiteSpec {
    @Override
    def getField(Provider<String> valueProvider) {
        ObservableCachedField<String> field = new ObservableCachedField<String>(MockFactory.getSameSessionIdProvider(),
                valueProvider,
                {} as SuccessListener<String>,
                {} as ErrorListener,
                DefaultExecutors.createDefaultValueGetterExecutor(),
                DefaultExecutors.createDefaultStateListenerExecutor()
        )
        return field
    }
}