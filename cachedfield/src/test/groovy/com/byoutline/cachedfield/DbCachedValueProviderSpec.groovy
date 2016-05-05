package com.byoutline.cachedfield

import com.byoutline.cachedfield.dbcache.DbCachedValueProvider
import com.byoutline.cachedfield.dbcache.DbWriter
import com.byoutline.cachedfield.dbcache.FetchType
import spock.lang.Shared
import spock.lang.Specification

import javax.inject.Provider

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class DbCachedValueProviderSpec extends Specification {
    @Shared
    String value = "value"
    @Shared
    String differentValue = "different value"

    def "should pass value from api to db"() {
        given:
        String result = null
        def apiProvider = MockFactory.getStringGetter(value)
        def dbSaver = { result = it } as DbWriter<String>
        def dbProvider = MockFactory.getStringGetter(differentValue)
        def provider = new DbCachedValueProvider(apiProvider, dbSaver, dbProvider)

        when:
        provider.get(FetchType.API)

        then:
        result == value
    }

    def "should return value from db even if api return something different"() {
        given:
        def apiProvider = MockFactory.getStringGetter(value)
        def dbSaver = {} as DbWriter<String>
        def dbProvider = MockFactory.getStringGetter(differentValue)
        def provider = new DbCachedValueProvider(apiProvider, dbSaver, dbProvider)

        when:
        String result = provider.get(FetchType.API)

        then:
        result == differentValue
    }

    def "should not call apiProvider or dbSaver if value from Db requested"() {
        given:
        boolean apiProvCalled = false
        boolean dbSaverCalled = false
        def apiProvider = { apiProvCalled = true; return value } as Provider<String>
        def dbSaver = { dbSaverCalled = true } as DbWriter<String>
        def dbProvider = MockFactory.getStringGetter(differentValue)
        def provider = new DbCachedValueProvider(apiProvider, dbSaver, dbProvider)

        when:
        provider.get(FetchType.DB)

        then:
        !apiProvCalled
        !dbSaverCalled
    }

    def "should pass runtime exception"() {
        given:
        def apiProvider = MockFactory.getStringGetter(value)
        def dbSaver = { throw new RuntimeException() } as DbWriter<String>
        def dbProvider = MockFactory.getStringGetter(differentValue)
        def provider = new DbCachedValueProvider(apiProvider, dbSaver, dbProvider)

        when:
        provider.get(FetchType.API)

        then:
        thrown RuntimeException
    }

    def "should allow different type for db and api"() {
        given:
        def apiProvider = MockFactory.getStringGetter(value)
        def dbSaver = {} as DbWriter<String>
        Provider<Integer> dbProvider = { return 1 } as Provider<Integer>
        def provider = new DbCachedValueProvider(apiProvider, dbSaver, dbProvider)

        when:
        def result = provider.get(FetchType.API)

        then:
        result == 1
    }
}
