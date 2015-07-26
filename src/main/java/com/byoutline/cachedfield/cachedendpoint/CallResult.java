package com.byoutline.cachedfield.cachedendpoint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CallResult<RETURN_TYPE> {
    @Nullable
    private final RETURN_TYPE successResult;
    @Nullable
    private final Exception failureResult;

    public CallResult(@Nullable RETURN_TYPE successResult, @Nullable Exception failureResult) {
        this.successResult = successResult;
        this.failureResult = failureResult;
    }

    public static <RETURN_TYPE> CallResult<RETURN_TYPE> successResult(@Nonnull RETURN_TYPE result) {
        return new CallResult<RETURN_TYPE>(result, null);
    }

    public static <RETURN_TYPE> CallResult<RETURN_TYPE> failureResult(@Nonnull Exception ex) {
        return new CallResult<RETURN_TYPE>(null, ex);
    }

    @Nullable
    public RETURN_TYPE getSuccessResult() {
        return successResult;
    }

    @Nullable
    public Exception getFailureResult() {
        return failureResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallResult<?> that = (CallResult<?>) o;

        if (successResult != null ? !successResult.equals(that.successResult) : that.successResult != null)
            return false;
        return !(failureResult != null ? !failureResult.equals(that.failureResult) : that.failureResult != null);

    }

    @Override
    public int hashCode() {
        int result = successResult != null ? successResult.hashCode() : 0;
        result = 31 * result + (failureResult != null ? failureResult.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CallResult{" +
                "successResult=" + successResult +
                ", failureResult=" + failureResult +
                '}';
    }
}
