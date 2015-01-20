package com.byoutline.cachedfield

import com.google.gson.reflect.TypeToken
import javax.inject.Provider
import retrofit.Callback
import com.byoutline.cachedfield.internal.StubErrorListener
import com.byoutline.cachedfield.internal.StubFieldStateListener
import com.byoutline.eventcallback.IBus
import com.byoutline.eventcallback.ResponseEvent
import com.byoutline.eventcallback.ResponseEventImpl
import com.byoutline.eventcallback.ResponseEventImpl

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

static SuccessListener<String> getSuccessListener() {
    return {} as SuccessListener<String>
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
    return new CachedFieldImpl(getSameSessionIdProvider(),
        getDelayedStringGetter(value, sleepTime), successListener, errorListener, 
        fieldStateListener)
}
static CachedField getLoadedCachedField(String value) {
    return getLoadedCachedField(value, new StubFieldStateListener())
}

static CachedField getLoadedCachedField(String value, FieldStateListener fieldStateListener) {
    return getLoadedCachedField(value, fieldStateListener, getSameSessionIdProvider())
}

static CachedField getLoadedCachedField(String value, FieldStateListener fieldStateListener, Provider<String> sessionIdProvider) {
    ResponseEvent<String> responseEvent = new ResponseEventImpl<String>()
    CachedField field = new CachedFieldImpl(sessionIdProvider,
        getStringGetter(value), getSuccessListener(), new StubErrorListener(), fieldStateListener)
    field.postValue()
    waitUntilFieldLoads(field)
    return field
}

static void waitUntilFieldLoads(CachedField field) {
    while(field.getState() != FieldState.LOADED) {
        sleep 1
    }
}
