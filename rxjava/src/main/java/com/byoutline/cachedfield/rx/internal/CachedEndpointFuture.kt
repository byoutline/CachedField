package com.byoutline.cachedfield.rx.internal

import com.byoutline.cachedfield.cachedendpoint.*
import com.byoutline.cachedfield.rx.CachedFieldResultAndArg
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

internal class CachedEndpointFuture<RETURN_TYPE: Any, ARG_TYPE>(private val field: CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE>,
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
        if (currentStateAndValue.arg == arg) setResult(currentStateAndValue.value)
    }

    private fun setResult(result: CallResult<RETURN_TYPE>) {
        synchronized(lock) {
            // We want to return result only once. If this.result is set
            // that means that we have already returned the result
            if (isDone) return
            this.result = result
            field.removeEndpointListener(endpointListener)
            lock.notifyAll()
        }
    }

    override fun isCancelled(): Boolean = cancelled

    override fun isDone(): Boolean = result != null

    override fun get(timeout: Long, unit: TimeUnit?): CachedFieldResultAndArg<RETURN_TYPE, ARG_TYPE> {
        synchronized(lock) {
            // Return early if we are already loaded
            val currentStateAndValue = field.stateAndValue
            if (currentStateAndValue.state == EndpointState.CALL_SUCCESS && arg == arg) {
                return convertResult(CallResult.create(currentStateAndValue.value.successResult, null))
            }
            result?.let { return convertResult(it) }
            // If we are not loaded, load (potentially async) value and wait for it.
            field.addEndpointListener(endpointListener)
            val millis = unit?.toMillis(timeout) ?: 0
            field.call(arg)
            lock.wait(millis)
        }
        return convertResult(result!!)
    }

    private fun convertResult(callResult: CallResult<RETURN_TYPE>): CachedFieldResultAndArg<RETURN_TYPE, ARG_TYPE> {
        callResult.failureResult?.let { throw it }
        val successResult = checkNotNull(callResult.successResult, {"CachedField Rx: Provider returned null value. This is not supported."})
        return CachedFieldResultAndArg(successResult, arg)
    }

    override fun get() = get(0, TimeUnit.MILLISECONDS)

    override fun cancel(p0: Boolean): Boolean {
        // XXX: Maybe we should cancel with arg?
        field.drop()
        cancelled = true
        setResult(CallResult.create(null, InterruptedException()))
        return true
    }
}