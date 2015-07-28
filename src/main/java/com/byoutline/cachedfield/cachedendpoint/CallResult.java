package com.byoutline.cachedfield.cachedendpoint;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

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
