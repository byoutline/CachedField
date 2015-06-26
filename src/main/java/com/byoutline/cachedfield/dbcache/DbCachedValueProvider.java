package com.byoutline.cachedfield.dbcache;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.internal.VoidArgumentFactory;

import javax.inject.Provider;


/**
 * Value provider that saves fetches/calculated value to db before returning it.
 * <p>
 * </p>
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class DbCachedValueProvider<RETURN_TYPE> implements ProviderWithArg<RETURN_TYPE, FetchType> {

    private final DbCachedValueProviderWithArg<RETURN_TYPE, Void> delegate;

    public DbCachedValueProvider(Provider<RETURN_TYPE> apiValueProvider,
                                 DbSaver<RETURN_TYPE> dbSaver,
                                 Provider<RETURN_TYPE> dbValueProvider) {
        ProviderWithArg<RETURN_TYPE, Void> apiValueProviderWithArg = VoidArgumentFactory.addVoidArg(apiValueProvider);
        DbSaverWithArg<RETURN_TYPE, Void> dbSaverWithArg = VoidArgumentFactory.addVoidArg(dbSaver);
        ProviderWithArg<RETURN_TYPE, Void> dbValueProviderWithArg = VoidArgumentFactory.addVoidArg(dbValueProvider);
        delegate = new DbCachedValueProviderWithArg<RETURN_TYPE, Void>(apiValueProviderWithArg,
                dbSaverWithArg, dbValueProviderWithArg);
    }

    @Override
    public RETURN_TYPE get(FetchType arg) {
        return delegate.get(new DbCacheArg<Void>(null, arg));
    }
}
