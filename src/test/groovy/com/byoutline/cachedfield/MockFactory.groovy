package com.byoutline.cachedfield

import com.byoutline.cachedfield.internal.StubErrorListener
import com.byoutline.cachedfield.internal.StubFieldStateListener
import com.byoutline.eventcallback.ResponseEvent
import com.byoutline.eventcallback.ResponseEventImpl

import javax.inject.Provider
import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

static Provider<String> getSameSessionIdProvider() {
    return { return "sessionId" } as Provider<String>
}

static Provider<String> getMultiSessionIdProvider() {
    int i = 1;
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
    return { value -> return } as SuccessListener<String>
}

static SuccessListenerWithArg<String, Integer> getSuccessListenerWithArg() {
    return { value, arg -> return } as SuccessListenerWithArg<String, Integer>
}

static ErrorListenerWithArg<Integer> getErrorListenerWithArg() {
    return { ex, arg -> return } as ErrorListenerWithArg<Integer>
}

static ExecutorService getAsyncFirstTaskSyncOtherExecutorService() {
    boolean executeAsync = true
    return [
            submit: {
                if(executeAsync) {
                    ((Thread) it).start()
                    executeAsync = false
                } else {
                    it.run()
                }
                return new FutureTask((Runnable) it, null)
            }
    ] as ExecutorService
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
    ResponseEvent<String> responseEvent = new ResponseEventImpl<String>()
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
            null
    )
    return field
}

static void waitUntilFieldLoads(CachedField field) {
    while (field.getState() != FieldState.LOADED) {
        sleep 1
    }
}

static void waitUntilFieldWithArgLoads(CachedFieldWithArg field) {
    def sleepCount = 0
    def maxSleepCount = 5000
    while (field.getState() != FieldState.LOADED && sleepCount < maxSleepCount) {
        sleep 1
        sleepCount++
    }
}

static void loadValue(CachedFieldWithArg<String, Integer> field, Integer arg) {
    field.postValue(arg)
    waitUntilFieldWithArgLoads(field)
}
