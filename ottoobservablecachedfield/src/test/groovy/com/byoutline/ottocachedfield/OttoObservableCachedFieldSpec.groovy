package com.byoutline.ottocachedfield

import com.byoutline.cachedfield.ErrorListener
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.SuccessListener
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.byoutline.cachedfield.testsuite.StateListenerSuiteSpec
import com.byoutline.observablecachedfield.ObservableCachedField
import com.google.common.util.concurrent.MoreExecutors

import javax.inject.Provider

class OttoObservableCachedFieldSpec extends StateListenerSuiteSpec{
    @Override
    def getField(Provider<String> valueProvider) {
        ObservableCachedField<String> field = new ObservableCachedField<String>(MockFactory.getSameSessionIdProvider(),
                valueProvider,
                {} as SuccessListener<String>,
                {} as ErrorListener,
                MoreExecutors.newDirectExecutorService(),
                DefaultExecutors.createDefaultStateListenerExecutor()
        )
        return field
    }
}