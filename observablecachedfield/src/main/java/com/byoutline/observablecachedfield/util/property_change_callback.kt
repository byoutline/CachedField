package com.byoutline.observablecachedfield.util

import android.databinding.Observable
import com.byoutline.cachedfield.cachedendpoint.EndpointState
import com.byoutline.cachedfield.cachedendpoint.StateAndValue
import com.byoutline.observablecachedfield.ObservableCachedFieldWithArg
import com.byoutline.observablecachedfield.util.AndroidExecutor.runInMainThread

/**
 * Registers [Observable.addOnPropertyChangedCallback] on success and error observables.
 * [onNext] or [onError] will be informed about changes in observables.
 * Returned callback has to be unregistered.
 */
fun <RETURN_TYPE, ARG_TYPE>
        ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>.registerChangeCallback(onNext: (RETURN_TYPE, ARG_TYPE) -> Any,
                                                                                   onError: (Exception, ARG_TYPE) -> Any = { _, _ -> }):
        Observable.OnPropertyChangedCallback {
    val callback = changeCallback(onNext, onError)
    val stateAndVal = stateAndValue
    observable().addOnPropertyChangedCallback(callback)
    observableError.addOnPropertyChangedCallback(callback)
    // Observable does inform listeners only if value changes.
    // If value was already loaded setting it back again to the same value will not inform callbacks,
    // so we check that case manually and pass state to onNext
    informNextIfNeeded(stateAndVal, onNext)
    return callback
}

private fun <RETURN_TYPE, ARG_TYPE> informNextIfNeeded(stateAndValue: StateAndValue<RETURN_TYPE, ARG_TYPE>,
                                                       onNext: (RETURN_TYPE, ARG_TYPE) -> Any) {
    if (stateAndValue.state != EndpointState.CALL_SUCCESS) return
    runInMainThread {
        onNext(stateAndValue.value.successResult!!, stateAndValue.arg!!)
    }
}

fun <RETURN_TYPE, ARG_TYPE>
        ObservableCachedFieldWithArg<RETURN_TYPE, ARG_TYPE>.changeCallback(onNext: (RETURN_TYPE, ARG_TYPE) -> Any,
                                                                           onError: (Exception, ARG_TYPE) -> Any)
        = object : Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(p0: Observable?, p1: Int) {
        if (p0 === observable()) {
            informNextIfNeeded(stateAndValue, onNext)
        }
        if (p0 === observableError) {
            stateAndValue.let {
                if (it.state != EndpointState.CALL_FAILED) return@let
                runInMainThread {
                    onError(it.value.failureResult!!, it.arg!!)
                }
            }
        }
    }
}

