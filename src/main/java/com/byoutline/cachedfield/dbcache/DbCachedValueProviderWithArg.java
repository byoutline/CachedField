package com.byoutline.cachedfield.dbcache;

import com.byoutline.cachedfield.ProviderWithArg;


/**
 * Value provider that saves fetches/calculated value to db before returning it.
 * <p>
 * </p>
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class DbCachedValueProviderWithArg<RETURN_TYPE, ARG_TYPE>
        implements ProviderWithArg<RETURN_TYPE, DbCacheArg<ARG_TYPE>> {

    private final ProviderWithArg<RETURN_TYPE, ARG_TYPE> apiValueProvider;
    private final DbSaverWithArg<RETURN_TYPE, ARG_TYPE> dbSaver;
    private final ProviderWithArg<RETURN_TYPE, ARG_TYPE> dbValueProvider;

    public DbCachedValueProviderWithArg(ProviderWithArg<RETURN_TYPE, ARG_TYPE> apiValueProvider,
                                        DbSaverWithArg<RETURN_TYPE, ARG_TYPE> dbSaver,
                                        ProviderWithArg<RETURN_TYPE, ARG_TYPE> dbValueProvider) {
        this.apiValueProvider = apiValueProvider;
        this.dbSaver = dbSaver;
        this.dbValueProvider = dbValueProvider;
    }

    @Override
    public RETURN_TYPE get(DbCacheArg<ARG_TYPE> arg) {
        ARG_TYPE provArg = arg.getArg();
        if(arg.getFetchType() == FetchType.API) {
            RETURN_TYPE valueFromApi = apiValueProvider.get(provArg);
            dbSaver.saveToDb(valueFromApi, provArg);
        }
        return dbValueProvider.get(provArg);
    }
}
