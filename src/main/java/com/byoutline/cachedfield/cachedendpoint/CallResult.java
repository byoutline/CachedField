package com.byoutline.cachedfield.cachedendpoint;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

/**
 * Simple storage class that keeps call result, that can be either success, fail, or none
 * (if call have not yet been made, or it did not end). Equals and hashcode compares all values.
 *
 * @param <RETURN_TYPE> Type of successful call result.
 */
@AutoValue
public abstract class CallResult<RETURN_TYPE> {

    public static <RETURN_TYPE> CallResult<RETURN_TYPE> create(@Nullable RETURN_TYPE successResult,
                                                               @Nullable Exception failureResult) {
        return new AutoValue_CallResult<RETURN_TYPE>(successResult, failureResult);
    }

    @Nullable
    public abstract RETURN_TYPE getSuccessResult();

    @Nullable
    public abstract Exception getFailureResult();
}
