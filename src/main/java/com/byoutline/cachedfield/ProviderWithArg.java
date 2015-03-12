package com.byoutline.cachedfield;


/**
 * @author Sebastian Kacprzak <nait at naitbit.com>
 * @param <RETURN_TYPE> Type of value returned.
 * @param <ARG_TYPE> Type of argument needed to calculate value.
 */
public interface ProviderWithArg<RETURN_TYPE, ARG_TYPE> {

    RETURN_TYPE get(ARG_TYPE arg);
}
