package com.byoutline.cachedfield

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
    doAndWaitForCachedFieldAction(field, true)
}

static void refreshAndWaitUntilFieldStopsLoading(CachedField field) {
    doAndWaitForCachedFieldAction(field, false)
}

private static void doAndWaitForCachedFieldAction(CachedField field, Boolean post) {
    boolean duringValueLoad = true
    def listener = { FieldState newState ->
        if (newState == FieldState.NOT_LOADED || newState == FieldState.LOADED) {
            duringValueLoad = false
        }
    } as FieldStateListener

    field.addStateListener(listener)
    if (post) {
        field.postValue()
    } else {
        field.refresh()
    }
    while (duringValueLoad) {
        sleep 1
    }
    field.removeStateListener(listener)
    // allow thread switch from state listener executor to success listener
    sleep 8
}