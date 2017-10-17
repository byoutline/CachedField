package com.byoutline.observablecachedfield

import com.byoutline.cachedfield.ErrorListener
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.SuccessListener
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.byoutline.cachedfield.testsuite.StateListenerSuiteSpec
import com.google.common.util.concurrent.MoreExecutors

class ObservableCachedFieldSpec extends StateListenerSuiteSpec {
    @Override
    def getField() {
        ObservableCachedField<String> field = new ObservableCachedField<String>(MockFactory.getSameSessionIdProvider(),
                MockFactory.getStringGetter("value"),
                {} as SuccessListener<String>,
                {} as ErrorListener,
                MoreExecutors.newDirectExecutorService(),
                DefaultExecutors.createDefaultStateListenerExecutor()
        )
        return field
    }

    @Override
    def waitUntilFieldFinishAction(Object field) {
    }
}