package com.byoutline.cachedfield

import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.FieldState
import com.byoutline.cachedfield.FieldStateListener

static <ARG_TYPE> void postAndWaitUntilFieldStopsLoading(CachedFieldWithArg<?, ARG_TYPE> field, ARG_TYPE arg) {
    boolean duringValueLoad = true
    def listener = { FieldState newState ->
        if (newState == FieldState.NOT_LOADED || newState == FieldState.LOADED) {
            duringValueLoad = false
        }
    } as FieldStateListener

    field.addStateListener(listener)
    field.postValue(arg)
    while (duringValueLoad) {
        sleep 1
    }
    field.removeStateListener(listener)
    sleep 8 // wait for event to be posted
}

static void postAndWaitUntilFieldStopsLoading(CachedField field) {
    boolean duringValueLoad = true
    def listener = { FieldState newState ->
        if (newState == FieldState.NOT_LOADED || newState == FieldState.LOADED) {
            duringValueLoad = false
        }
    } as FieldStateListener

    field.addStateListener(listener)
    field.postValue()
    while (duringValueLoad) {
        sleep 1
    }
    field.removeStateListener(listener)
    // allow thread switch from state listener executor to success listener
    sleep 8
}