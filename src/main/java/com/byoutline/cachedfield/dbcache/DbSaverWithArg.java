package com.byoutline.cachedfield.dbcache;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface DbSaverWithArg<RETURN_TYPE, ARG_TYPE> {
    void saveToDb(RETURN_TYPE value, ARG_TYPE arg);
}
