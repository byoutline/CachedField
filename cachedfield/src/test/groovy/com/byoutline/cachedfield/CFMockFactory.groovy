package com.byoutline.cachedfield

import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArgImpl
import com.byoutline.cachedfield.cachedendpoint.CallEndListener
import com.byoutline.cachedfield.cachedendpoint.StateAndValue
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.byoutline.cachedfield.internal.StubErrorListener
import com.byoutline.cachedfield.internal.StubFieldStateListener
import com.google.common.util.concurrent.MoreExecutors

import javax.inject.Provider
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

static Provider<String> getSameSessionIdProvider() {
    return { return "sessionId" } as Provider<String>
}

static Provider<String> getMultiSessionIdProvider() {
    int i = 1
    return { return "sessionId" + i++ } as Provider<String>
}

static Provider<String> getDelayedStringGetter(String value) {
    return getDelayedStringGetter(value, 5)
}

static Provider<String> getDelayedStringGetter(String value, long sleepTime) {
    return { Thread.sleep(sleepTime); return value } as Provider<String>
}

static Provider<String> getStringGetter(String value) {
    return { return value } as Provider<String>
}

static ProviderWithArg<String, Integer> getDelayedStringIntGetter(Map<Integer, String> argToValueMap, long sleepTime) {
    return { key -> Thread.sleep(sleepTime); return argToValueMap.get(key) } as ProviderWithArg<String, Integer>
}

static ProviderWithArg<String, Integer> getStringIntGetter(Map<Integer, String> argToValueMap) {
    return { key -> return argToValueMap.get(key) } as ProviderWithArg<String, Integer>
}

static SuccessListener<String> getSuccessListener() {
    return { value -> } as SuccessListener<String>
}

static SuccessListenerWithArg<String, Integer> getSuccessListenerWithArg() {
    return { value, arg -> } as SuccessListenerWithArg<String, Integer>
}

static ErrorListenerWithArg<Integer> getErrorListenerWithArg() {
    return { ex, arg -> } as ErrorListenerWithArg<Integer>
}

static ExecutorService getAsyncFirstTaskSyncOtherExecutorService() {
    boolean executeAsync = true
    return [
            submit: {
                if (executeAsync) {
                    ((Thread) it).start()
                    executeAsync = false
                } else {
                    it.run()
                }
                return new FutureTask((Runnable) it, null)
            }
    ] as ExecutorService
}

static CachedField<String> getCachedFieldBlockingVal() {
    return new CachedFieldImpl<String>(
            getSameSessionIdProvider(),
            getStringGetter("val"),
            getSuccessListener(),
            {} as ErrorListener,
            MoreExecutors.newDirectExecutorService(),
            DefaultExecutors.createDefaultStateListenerExecutor())
}

static CachedField<String> getCachedField(String value, ErrorListener errorHandler) {
    return new CachedFieldImpl<String>(
            getSameSessionIdProvider(),
            getStringGetter(value),
            getSuccessListener(),
            errorHandler
    )
}

static CachedField getDelayedCachedField(String value, SuccessListener<String> successListener) {
    return getDelayedCachedField(value, 5, successListener)
}

static CachedField getDelayedCachedField(String value, FieldStateListener fieldStateListener) {
    return getDelayedCachedField(value, 5, getSuccessListener(), new StubErrorListener(), fieldStateListener)
}

static CachedField getDelayedCachedField(String value, long sleepTime, SuccessListener<String> successListener) {
    return getDelayedCachedField(value, sleepTime, successListener, new StubErrorListener(), new StubFieldStateListener())
}

static CachedField getDelayedCachedField(String value, long sleepTime,
                                         SuccessListener<String> successListener, ErrorListener errorListener,
                                         FieldStateListener fieldStateListener) {
    CachedField field = new CachedFieldImpl(getSameSessionIdProvider(),
            getDelayedStringGetter(value, sleepTime), successListener, errorListener)
    field.addStateListener(fieldStateListener)
    return field
}

static CachedField getLoadedCachedField(String value) {
    return getLoadedCachedField(value, new StubFieldStateListener())
}

static CachedField getLoadedCachedField(String value, FieldStateListener fieldStateListener) {
    return getLoadedCachedField(value, fieldStateListener, getSameSessionIdProvider())
}

static CachedField getLoadedCachedField(String value, FieldStateListener fieldStateListener, Provider<String> sessionIdProvider) {
    CachedField field = new CachedFieldImpl(sessionIdProvider,
            getStringGetter(value), getSuccessListener(), new StubErrorListener())
    field.postValue()
    waitUntilFieldLoads(field)
    field.addStateListener(fieldStateListener)
    return field
}

static CallEndListener<String, Integer> getStubCallEndListener() {
    { StateAndValue<String, Integer> callResult -> } as CallEndListener<String, Integer>
}

static CachedEndpointWithArgImpl<String, Integer> getCachedEndpointBlockingVal() {
    return getCachedEndpointBlockingValueProv([arg: 'val'] as Map<Integer, String>)
}

static CachedEndpointWithArgImpl<String, Integer> getDelayedCachedEndpoint(Map<Integer, String> argToValueMap, Long sleepTime) {
    return new CachedEndpointWithArgImpl(
            getSameSessionIdProvider(),
            getDelayedStringIntGetter(argToValueMap, sleepTime),
            getStubCallEndListener(),
            DefaultExecutors.createDefaultValueGetterExecutor(),
            DefaultExecutors.createDefaultStateListenerExecutor()
    )
}

static CachedEndpointWithArgImpl<String, Integer> getCachedEndpointBlockingValueProv(Map<Integer, String> argToValueMap) {
    return new CachedEndpointWithArgImpl(
            getSameSessionIdProvider(),
            getStringIntGetter(argToValueMap),
            getStubCallEndListener(),
            MoreExecutors.newDirectExecutorService(),
            DefaultExecutors.createDefaultStateListenerExecutor()
    )
}

static CachedEndpointWithArgImpl<String, Integer> getCachedEndpointBlocking(Map<Integer, String> argToValueMap) {
    return new CachedEndpointWithArgImpl(
            getSameSessionIdProvider(),
            getStringIntGetter(argToValueMap),
            getStubCallEndListener(),
            MoreExecutors.newDirectExecutorService(),
            { it.run() } as Executor
    )
}

static CachedFieldWithArg getCachedFieldWithArgBlockingVal() {
    return getCachedFieldWithArg([arg: 'val'] as Map<Integer, String>, MoreExecutors.newDirectExecutorService())
}

static CachedFieldWithArg getCachedFieldWithArg(Map<Integer, String> argToValueMap) {
    return getCachedFieldWithArg(argToValueMap, getSuccessListenerWithArg())
}

static CachedFieldWithArg getCachedFieldWithArg(Map<Integer, String> argToValueMap, ExecutorService valueProviderExecutor) {
    return getCachedFieldWithArg(argToValueMap, getSuccessListenerWithArg(), getErrorListenerWithArg(), valueProviderExecutor)
}

static CachedFieldWithArg getCachedFieldWithArg(Map<Integer, String> argToValueMap, SuccessListenerWithArg<String, Integer> successListener) {
    return getCachedFieldWithArg(argToValueMap, successListener, getErrorListenerWithArg())
}

static CachedFieldWithArg getCachedFieldWithArg(Map<Integer, String> argToValueMap, ErrorListenerWithArg<Integer> errorListenerWithArg) {
    return getCachedFieldWithArg(argToValueMap, getSuccessListenerWithArg(), errorListenerWithArg)
}

static CachedFieldWithArg getCachedFieldWithArg(Map<Integer, String> argToValueMap, SuccessListenerWithArg<String, Integer> successListener, ErrorListenerWithArg<Integer> errorListenerWithArg) {
    CachedFieldWithArg field = new CachedFieldWithArgImpl(getSameSessionIdProvider(),
            getStringIntGetter(argToValueMap),
            successListener,
            errorListenerWithArg
    )
    return field
}

static CachedFieldWithArg getCachedFieldWithArg(Map<Integer, String> argToValueMap, SuccessListenerWithArg<String, Integer> successListener, ErrorListenerWithArg<Integer> errorListenerWithArg, ExecutorService valueProviderExecutor) {
    CachedFieldWithArg field = new CachedFieldWithArgImpl(getSameSessionIdProvider(),
            getStringIntGetter(argToValueMap),
            successListener,
            errorListenerWithArg,
            valueProviderExecutor,
            DefaultExecutors.createDefaultStateListenerExecutor()
    )
    return field
}

static void waitUntilFieldLoads(CachedField field) {
    waitUntilFieldReachesState(field, FieldState.LOADED)
    Thread.sleep(2) // wait for success listener to get informed
}

static void waitUntilFieldReachesState(CachedField field, FieldState state) {
    def sleepCount = 0
    def maxSleepCount = 5000
    while (field.getState() != state && sleepCount < maxSleepCount) {
        Thread.sleep(1)
        sleepCount++
    }
}

static void waitUntilFieldWithArgLoads(CachedFieldWithArg field) {
    def sleepCount = 0
    def maxSleepCount = 5000
    while (field.getState() != FieldState.LOADED && sleepCount < maxSleepCount) {
        Thread.sleep(1)
        sleepCount++
    }
    Thread.sleep(2) // wait for success listener to get informed
}

static void loadValue(CachedFieldWithArg<String, Integer> field, Integer arg) {
    field.postValue(arg)
    waitUntilFieldWithArgLoads(field)
}