package com.byoutline.cachedfield.internal;

import com.byoutline.cachedfield.ErrorListener;
import com.byoutline.cachedfield.ErrorListenerWithArg;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.SuccessListener;
import com.byoutline.cachedfield.SuccessListenerWithArg;
import com.byoutline.cachedfield.dbcache.DbWriter;
import com.byoutline.cachedfield.dbcache.DbWriterWithArg;

import javax.annotation.Nonnull;
import javax.inject.Provider;

/**
 * Utility that converts classes without args to classes with Void arg.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public final class VoidArgumentFactory {

    private VoidArgumentFactory() {
    }

    public static <RETURN_TYPE> ProviderWithArg<RETURN_TYPE, Void> addVoidArg(
            @Nonnull final Provider<RETURN_TYPE> valueGetter) {
        return new ProviderWithArg<RETURN_TYPE, Void>() {

            @Override
            public RETURN_TYPE get(Void arg) {
                return valueGetter.get();
            }
        };
    }

    public static <RETURN_TYPE> SuccessListenerWithArg<RETURN_TYPE, Void> addVoidArg(
            @Nonnull final SuccessListener<RETURN_TYPE> successHandler) {
        return new SuccessListenerWithArg<RETURN_TYPE, Void>() {

            @Override
            public void valueLoaded(RETURN_TYPE value, Void arg) {
                successHandler.valueLoaded(value);
            }
        };
    }

    public static <RETURN_TYPE> ErrorListenerWithArg<Void> addVoidArg(
            @Nonnull final ErrorListener errorHandler) {
        return new ErrorListenerWithArg<Void>() {

            @Override
            public void valueLoadingFailed(Exception ex, Void arg) {
                errorHandler.valueLoadingFailed(ex);
            }
        };
    }

    public static <RETURN_TYPE> DbWriterWithArg<RETURN_TYPE, Void> addVoidArg(
            @Nonnull final DbWriter<RETURN_TYPE> dbWriter) {
        return new DbWriterWithArg<RETURN_TYPE, Void>() {

            @Override
            public void saveToDb(RETURN_TYPE value, Void arg) {
                dbWriter.saveToDb(value);
            }
        };
    }
}
