package com.byoutline.cachedfield;

import retrofit.Callback;

/**
 * Fetches value asynchronically.
 *
 * @author Sebastian Kacprzak <nait at naitbit.com>
 */
public interface FieldGetter<T> {

    void get(Callback<T> cb);
}
