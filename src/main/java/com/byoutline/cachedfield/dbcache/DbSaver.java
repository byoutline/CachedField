package com.byoutline.cachedfield.dbcache;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public interface DbSaver<RETURN_TYPE> {
    void saveToDb(RETURN_TYPE value);
}
