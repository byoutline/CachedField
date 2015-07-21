package com.byoutline.cachedfield.dbcache;

import com.byoutline.cachedfield.ProviderWithArg;


/**
 * Value provider that saves fetches/calculated value to db before returning it.
 * <p>
 * </p>
 *
 * @param <API_RETURN_TYPE> Type of value returned by API
 * @param <DB_RETURN_TYPE>  Type of value returned by DB
 * @param <ARG_TYPE>        Type of argument needed to calculate value
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class DbCachedValueProviderWithArg<API_RETURN_TYPE, DB_RETURN_TYPE, ARG_TYPE>
        implements ProviderWithArg<DB_RETURN_TYPE, DbCacheArg<ARG_TYPE>> {

    private final ProviderWithArg<API_RETURN_TYPE, ARG_TYPE> apiValueProvider;
    private final DbWriterWithArg<API_RETURN_TYPE, ARG_TYPE> dbSaver;
    private final ProviderWithArg<DB_RETURN_TYPE, ARG_TYPE> dbValueProvider;

    public DbCachedValueProviderWithArg(ProviderWithArg<API_RETURN_TYPE, ARG_TYPE> apiValueProvider,
                                        DbWriterWithArg<API_RETURN_TYPE, ARG_TYPE> dbSaver,
                                        ProviderWithArg<DB_RETURN_TYPE, ARG_TYPE> dbValueProvider) {
        this.apiValueProvider = apiValueProvider;
        this.dbSaver = dbSaver;
        this.dbValueProvider = dbValueProvider;
    }

    @Override
    public DB_RETURN_TYPE get(DbCacheArg<ARG_TYPE> arg) {
        ARG_TYPE provArg = arg.getArg();
        if (arg.getFetchType() == FetchType.API) {
            API_RETURN_TYPE valueFromApi = apiValueProvider.get(provArg);
            dbSaver.saveToDb(valueFromApi, provArg);
        }
        return dbValueProvider.get(provArg);
    }
}
