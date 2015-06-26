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

    public DbCachedValueProvider(Provider<RETURN_TYPE> apiFetcher,
                                 DbWriter<RETURN_TYPE> dbWriter,
                                 Provider<RETURN_TYPE> dbReader) {
        ProviderWithArg<RETURN_TYPE, Void> apiFetcherWithArg = VoidArgumentFactory.addVoidArg(apiFetcher);
        DbSaverWithArg<RETURN_TYPE, Void> dbWriterWithArg = VoidArgumentFactory.addVoidArg(dbWriter);
        ProviderWithArg<RETURN_TYPE, Void> dbReaderWithArg = VoidArgumentFactory.addVoidArg(dbReader);
        delegate = new DbCachedValueProviderWithArg<RETURN_TYPE, Void>(apiFetcherWithArg,
                dbWriterWithArg, dbReaderWithArg);
    }

    @Override
    public RETURN_TYPE get(FetchType arg) {
        return delegate.get(new DbCacheArg<Void>(null, arg));
    }
}
