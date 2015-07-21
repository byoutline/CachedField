package com.byoutline.cachedfield.dbcache;

import javax.annotation.Nonnull;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class DbCacheArg<ARG_TYPE> {
    private final ARG_TYPE arg;
    private final FetchType fetchType;

    public DbCacheArg(ARG_TYPE arg, @Nonnull FetchType fetchType) {
        this.arg = arg;
        this.fetchType = fetchType;
    }

    public ARG_TYPE getArg() {
        return arg;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbCacheArg<?> that = (DbCacheArg<?>) o;

        if (!arg.equals(that.arg)) return false;
        return fetchType == that.fetchType;

    }

    @Override
    public int hashCode() {
        int result = arg.hashCode();
        result = 31 * result + (fetchType != null ? fetchType.hashCode() : 0);
        return result;
    }
}
