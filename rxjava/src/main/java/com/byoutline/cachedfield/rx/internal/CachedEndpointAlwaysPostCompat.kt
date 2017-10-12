package com.byoutline.cachedfield.rx.internal

import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg
import com.byoutline.cachedfield.cachedendpoint.EndpointStateListener
import com.byoutline.cachedfield.cachedendpoint.StateAndValue

/**
 * Wraps [CachedFieldWithArg] to have same API as the [CachedEndpointWithArg]. However [call] is implemented
 * as [CachedFieldWithArg.postValue], so it does not behave like idiomatic [CachedEndpointWithArg] (which would call
 * [CachedFieldWithArg.refresh] instead.
 */
internal class CachedEndpointAlwaysPostCompat<RETURN_TYPE, ARG_TYPE>(private val field: CachedFieldWithArg<RETURN_TYPE, ARG_TYPE>)
    : CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE> {

    override fun getStateAndValue(): StateAndValue<RETURN_TYPE, ARG_TYPE> = field.stateAndValue

    override fun call(arg: ARG_TYPE) = field.postValue(arg)

    override fun drop() = field.drop()

    override fun addEndpointListener(listener: EndpointStateListener<RETURN_TYPE, ARG_TYPE>)
            = field.addEndpointListener(listener)

    override fun removeEndpointListener(listener: EndpointStateListener<RETURN_TYPE, ARG_TYPE>): Boolean
            = field.removeEndpointListener(listener)
}
