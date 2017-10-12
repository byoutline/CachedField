package com.byoutline.cachedfield.rx

import com.byoutline.cachedfield.CachedField
import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg
import com.byoutline.cachedfield.rx.internal.CachedEndpointFuture
import com.byoutline.cachedfield.rx.internal.CachedEndpointAlwaysPostCompat

fun <RETURN_TYPE, ARG_TYPE> CachedFieldWithArg<RETURN_TYPE, ARG_TYPE>.postToRx(arg: ARG_TYPE?)
        : rx.Single<CachedFieldResultAndArg<RETURN_TYPE, ARG_TYPE?>> {
    return CachedEndpointAlwaysPostCompat(this).postToRx(arg)
}

fun <RETURN_TYPE> CachedField<RETURN_TYPE>.postToRx(): rx.Single<RETURN_TYPE> {
    return toCachedFieldWithArg().postToRx(null).map { it.result }
}

fun <RETURN_TYPE, ARG_TYPE> CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE>.postToRx(arg: ARG_TYPE?)
        : rx.Single<CachedFieldResultAndArg<RETURN_TYPE, ARG_TYPE?>> {
    return rx.Single.from(CachedEndpointFuture(this, arg))
}

data class CachedFieldResultAndArg<out RETURN_TYPE, out ARG_TYPE>(val result: RETURN_TYPE, val arg: ARG_TYPE?)

