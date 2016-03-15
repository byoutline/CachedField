package com.byoutline.cachedfield.dbcache;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.internal.VoidArgumentFactory;

import javax.inject.Provider;


/**
 * Value provider that saves fetches/calculated value to db before returning it.
 * <p>
 * </p>
 *
 * @param <API_RETURN_TYPE> Type of value returned by API
 * @param <DB_RETURN_TYPE>  Type of value returned by DB
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class DbCachedValueProvider<API_RETURN_TYPE, DB_RETURN_TYPE> implements ProviderWithArg<DB_RETURN_TYPE, FetchType> {

    private final DbCachedValueProviderWithArg<API_RETURN_TYPE, DB_RETURN_TYPE, Void> delegate;

    public DbCachedValueProvider(Provider<API_RETURN_TYPE> apiFetcher,
                                 DbWriter<API_RETURN_TYPE> dbWriter,
                                 Provider<DB_RETURN_TYPE> dbReader) {
        ProviderWithArg<API_RETURN_TYPE, Void> apiFetcherWithArg = VoidArgumentFactory.addVoidArg(apiFetcher);
        DbWriterWithArg<API_RETURN_TYPE, Void> dbWriterWithArg = VoidArgumentFactory.addVoidArg(dbWriter);
        ProviderWithArg<DB_RETURN_TYPE, Void> dbReaderWithArg = VoidArgumentFactory.addVoidArg(dbReader);
        delegate = new DbCachedValueProviderWithArg<API_RETURN_TYPE, DB_RETURN_TYPE, Void>(apiFetcherWithArg,
                dbWriterWithArg, dbReaderWithArg);
    }

    @Override
    public DB_RETURN_TYPE get(FetchType arg) {
        return delegate.get(DbCacheArg.<Void>create(null, arg));
    }
}
