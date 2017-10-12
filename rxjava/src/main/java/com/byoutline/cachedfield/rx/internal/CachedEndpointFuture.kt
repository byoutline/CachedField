package com.byoutline.cachedfield.rx.internal

import com.byoutline.cachedfield.cachedendpoint.*
import com.byoutline.cachedfield.rx.CachedFieldResultAndArg
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

internal class CachedEndpointFuture<RETURN_TYPE, ARG_TYPE>(private val field: CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE>,
                                                           private val arg: ARG_TYPE?)
    : Future<CachedFieldResultAndArg<RETURN_TYPE, ARG_TYPE?>> {
    private var result: CallResult<RETURN_TYPE>? = null
    private var cancelled = false
    private val lock = java.lang.Object()
    private val endpointListener = EndpointStateListener<RETURN_TYPE, ARG_TYPE?> { currentStateAndValue ->
        when (currentStateAndValue.state) {
            EndpointState.BEFORE_CALL -> {
            }
            EndpointState.DURING_CALL -> {
            }
            EndpointState.CALL_SUCCESS -> setResultIfArgMatches(currentStateAndValue)
            EndpointState.CALL_FAILED -> setResultIfArgMatches(currentStateAndValue)
        }
    }

    private fun setResultIfArgMatches(currentStateAndValue: StateAndValue<RETURN_TYPE, ARG_TYPE?>) {
        if(currentStateAndValue.arg == arg) setResult(currentStateAndValue.value)
    }

    private fun setResult(result: CallResult<RETURN_TYPE>) {
        synchronized(lock) {
            if (this.result != null) return
            this.result = result
            field.removeEndpointListener(endpointListener)
            lock.notifyAll()
        }
    }

    override fun isCancelled(): Boolean = cancelled

    override fun isDone(): Boolean = result != null

    override fun get(p0: Long, p1: TimeUnit?): CachedFieldResultAndArg<RETURN_TYPE, ARG_TYPE> {
        synchronized(lock) {
            // Return early if we are already loaded
            val currentStateAndValue = field.stateAndValue
            if (currentStateAndValue.state == EndpointState.CALL_SUCCESS && arg == arg) {
                return CachedFieldResultAndArg(currentStateAndValue.value.successResult!!, arg)
            }
            // If we are not loaded, load (potentially async) value and wait for it.
            field.addEndpointListener(endpointListener)
            val millis = p1?.toMillis(p0) ?: 0
            field.call(arg)
            lock.wait(millis)
        }
        result!!.let {
            it.failureResult?.let { throw it }
            return CachedFieldResultAndArg(it.successResult!!, arg)
        }
    }

    override fun get() = get(0, TimeUnit.MILLISECONDS)

    override fun cancel(p0: Boolean): Boolean {
        setResult(CallResult.create(null, InterruptedException()))
        return true
    }
}