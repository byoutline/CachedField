package com.byoutline.cachedfield.dbcache;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
@AutoValue
public abstract class DbCacheArg<ARG_TYPE> {

    public static <ARG_TYPE> DbCacheArg<ARG_TYPE> create(@Nullable ARG_TYPE arg, @Nonnull FetchType fetchType) {
        return new AutoValue_DbCacheArg<ARG_TYPE>(arg, fetchType);
    }

    @Nullable
    public abstract ARG_TYPE getArg();

    @Nonnull
    public abstract FetchType getFetchType();
}
