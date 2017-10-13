package com.byoutline.eventbuscachedfield

import com.byoutline.cachedfield.MockCachedFieldLoader
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.dbcache.DbWriter
import com.byoutline.cachedfield.dbcache.FetchType
import com.byoutline.ibuscachedfield.events.ResponseEventWithArgImpl
import de.greenrobot.event.EventBus
import spock.lang.Shared
import spock.lang.Specification

import javax.inject.Provider

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class DbCacheSpec extends Specification {
    @Shared
    String value = "value"
    @Shared
    String differentValue = "different value"
    ResponseEventWithArgImpl<String, FetchType> successEvent
    ResponseEventWithArgImpl<Exception, FetchType> errorEvent
    EventBus bus

    def setup() {
        bus = Mock()
        successEvent = new ResponseEventWithArgImpl<>()
        errorEvent = new ResponseEventWithArgImpl<>()

        EventBusCachedField.init(MockFactory.getSameSessionIdProvider(), bus)
    }


    def "should post value from API"() {
        given:
        def dbSaver = {} as DbWriter
        EventBusCachedFieldWithArg<String, FetchType> field = EventBusCachedField.<String> builder()
                .withApiFetcher(MockFactory.getStringGetter(value))
                .withDbWriter(dbSaver)
                .withDbReader(MockFactory.getStringGetter(value))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build()

        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field, FetchType.API)

        then:
        value == successEvent.getResponse()
        FetchType.API == successEvent.getArgValue()
    }

    def "should post value from DB"() {
        given:
        def dbSaver = {} as DbWriter
        EventBusCachedFieldWithArg<String, FetchType> field = EventBusCachedField.<String> builder()
                .withApiFetcher(MockFactory.getStringGetter(value))
                .withDbWriter(dbSaver)
                .withDbReader(MockFactory.getStringGetter(differentValue))
                .withSuccessEvent(successEvent)
                .withResponseErrorEvent(errorEvent)
                .build()

        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field, FetchType.DB)

        then:
        differentValue == successEvent.getResponse()
        FetchType.DB == successEvent.getArgValue()
    }

    def "should allow different return type for DB and API"() {
        given:
        def dbSaver = {} as DbWriter
        def event = new ResponseEventWithArgImpl<Integer, FetchType>()
        EventBusCachedFieldWithArg<Integer, FetchType> field = EventBusCachedField.<String> builder()
                .withApiFetcher(MockFactory.getStringGetter(value))
                .withDbWriter(dbSaver)
                .withDbReader({ return 1 } as Provider<Integer>)
                .withSuccessEvent(event)
                .build()

        when:
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field, FetchType.API)

        then:
        event.getResponse() == 1
    }
}
