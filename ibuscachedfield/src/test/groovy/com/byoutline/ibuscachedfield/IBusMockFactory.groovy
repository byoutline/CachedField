package com.byoutline.ibuscachedfield

import com.byoutline.cachedfield.CachedField
import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.byoutline.eventcallback.IBus
import com.byoutline.ibuscachedfield.mocks.CachedEndpointWithArgConstructorWrapperImpl
import com.byoutline.ibuscachedfield.mocks.CachedFieldConstructorWrapperImpl
import com.byoutline.ibuscachedfield.mocks.CachedFieldWithArgConstructorWrapperImpl

static IBusCachedFieldWithArgBuilder<String, Integer, IBus, CachedFieldWithArg<String, Integer>> fieldWithArgBuilder(IBus bus) {
    new IBusCachedFieldWithArgBuilder<String, Integer, IBus, CachedFieldWithArg<String, Integer>>(new CachedFieldWithArgConstructorWrapperImpl(), bus,
            MockFactory.getSameSessionIdProvider(),
            DefaultExecutors.createDefaultValueGetterExecutor(),
            DefaultExecutors.createDefaultStateListenerExecutor()) {}
}

static IBusCachedFieldBuilder<String, IBus, CachedField<String>> fieldWithoutArgBuilder(IBus bus) {
    new IBusCachedFieldBuilder<String, IBus, CachedField<String>>(new CachedFieldConstructorWrapperImpl(), bus,
            MockFactory.getSameSessionIdProvider(),
            DefaultExecutors.createDefaultValueGetterExecutor(),
            DefaultExecutors.createDefaultStateListenerExecutor()) {}
}

static IBusCachedEndpointWithArgBuilder<String, Integer, IBus, CachedEndpointWithArg<String, Integer>> endpointWithArgBuilder(IBus bus) {
    new IBusCachedEndpointWithArgBuilder<String, Integer, IBus, CachedEndpointWithArg<String, Integer>>(new CachedEndpointWithArgConstructorWrapperImpl(), bus,
            MockFactory.getSameSessionIdProvider(),
            DefaultExecutors.createDefaultValueGetterExecutor(),
            DefaultExecutors.createDefaultStateListenerExecutor()) {}
}

