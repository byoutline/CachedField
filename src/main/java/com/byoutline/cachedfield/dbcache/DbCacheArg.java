package com.byoutline.cachedfield.dbcache;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class DbCacheArg<ARG_TYPE> {
    private final ARG_TYPE arg;
    private final FetchType fetchType;

    public DbCacheArg(ARG_TYPE arg, FetchType fetchType) {
        this.arg = arg;
        this.fetchType = fetchType;
    }

    public ARG_TYPE getArg() {
        return arg;
    }

    public FetchType getFetchType() {
        return fetchType;
    }
}
