package com.byoutline.cachedfield

import com.google.gson.reflect.TypeToken
import javax.inject.Provider
import retrofit.Callback
import com.byoutline.cachedfield.internal.StubErrorListener
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

static CachedField getDelayedCachedField(String value, SuccessListener<String> successListener) {
    return getDelayedCachedField(value, 5, successListener)
}

static CachedField getDelayedCachedField(String value, long sleepTime, SuccessListener<String> successListener) {
    return getDelayedCachedField(value, sleepTime, successListener, new StubErrorListener())
}

static CachedField getDelayedCachedField(String value, long sleepTime, 
    SuccessListener<String> successListener, ErrorListener errorListener) {
    ResponseEvent<String> responseEvent = new ResponseEventImpl<String>()
    return new CachedFieldImpl(getSameSessionIdProvider(),
        getDelayedStringGetter(value, sleepTime), successListener, errorListener)
}

static CachedField getLoadedCachedField(String value) {
    ResponseEvent<String> responseEvent = new ResponseEventImpl<String>()
    SuccessListener<String> successListener = {} as SuccessListener<String>
    CachedField field = new CachedFieldImpl(getSameSessionIdProvider(),
        getStringGetter(value), successListener, new StubErrorListener())
    field.postValue()
    Thread.sleep(1)
    return field
}

