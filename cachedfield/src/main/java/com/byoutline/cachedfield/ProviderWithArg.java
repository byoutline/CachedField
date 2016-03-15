package com.byoutline.cachedfield;


/**
 * @param <RETURN_TYPE> Type of value returned.
 * @param <ARG_TYPE>    Type of argument needed to calculate value.
 * @author Sebastian Kacprzak <nait at naitbit.com>
 */
public interface ProviderWithArg<RETURN_TYPE, ARG_TYPE> {

    RETURN_TYPE get(ARG_TYPE arg);
}
