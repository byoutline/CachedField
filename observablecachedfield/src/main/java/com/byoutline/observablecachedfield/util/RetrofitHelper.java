package com.byoutline.observablecachedfield.util;

import com.byoutline.cachedfield.ProviderWithArg;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Provider;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Contains helper methods that allow for use of <code>Retrofit 2</code> with
 * CachedField with shorter syntax.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class RetrofitHelper {
    private static MsgDisplayer MSG_DISPLAYER = null;

    public static void setMsgDisplayer(MsgDisplayer msgDisplayer) {
        MSG_DISPLAYER = msgDisplayer;
    }

    @Nonnull
    public static <RETURN_TYPE> Provider<RETURN_TYPE> apiValueProv(
            final Provider<Call<RETURN_TYPE>> prov) {
        return new Provider<RETURN_TYPE>() {
            @Override
            public RETURN_TYPE get() {
                try {
                    Response<RETURN_TYPE> resp = prov.get().execute();
                    if (resp.isSuccessful()) {
                        return resp.body();
                    } else {
                        throw logAndWrapIntoException(resp);
                    }
                } catch (IOException e) {
                    throw logAndWrapIntoException(e);
                }
            }
        };
    }


    @Nonnull
    public static <RETURN_TYPE, ARG_TYPE> ProviderWithArg<RETURN_TYPE, ARG_TYPE> apiValueProv(
            final ProviderWithArg<Call<RETURN_TYPE>, ARG_TYPE> prov) {
        return new ProviderWithArg<RETURN_TYPE, ARG_TYPE>() {
            @Override
            public RETURN_TYPE get(ARG_TYPE arg) {
                try {
                    Response<RETURN_TYPE> resp = prov.get(arg).execute();
                    if (resp.isSuccessful()) {
                        return resp.body();
                    } else {
                        throw logAndWrapIntoException(resp);
                    }
                } catch (IOException e) {
                    throw logAndWrapIntoException(e);
                }
            }
        };
    }

    private static ApiException logAndWrapIntoException(Response error) {
        logError(error.errorBody());
        return new ApiException(error);
    }

    private static ApiException logAndWrapIntoException(IOException e) {
        logError(e);
        return new ApiException(e);
    }

    private static void logError(@Nullable ResponseBody error) {
        if (error != null) {
            logError(error.toString());
        }
    }

    private static void logError(Exception error) {
        String locMsg = error.getLocalizedMessage();
        logError(locMsg);
    }

    private static void logError(String locMsg) {
        if (MSG_DISPLAYER != null && locMsg != null && !locMsg.isEmpty()) {
            MSG_DISPLAYER.showMsg(locMsg);
        }
    }

    public static class ApiException extends RuntimeException {
        @Nullable
        public final Response errorResponse;

        public ApiException(Response errorResponse) {
            this.errorResponse = errorResponse;
        }

        public ApiException(Throwable throwable) {
            super(throwable);
            errorResponse = null;
        }
    }

    public interface MsgDisplayer {
        void showMsg(String msg);
    }
}
