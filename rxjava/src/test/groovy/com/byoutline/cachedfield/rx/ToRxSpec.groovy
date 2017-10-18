package com.byoutline.cachedfield.rx

import com.byoutline.cachedfield.*
import rx.Single
import rx.schedulers.Schedulers
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout

import javax.inject.Provider

class ToRxSpec extends Specification {
    @Shared
    String value = "value"
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']
    SuccessListener<String> stubSuccessListener = {} as SuccessListener<String>
    ErrorListener stubErrorListener = {} as ErrorListener

    @Timeout(value = 1)
    def "should wait for a value"() {
        given:
        CachedField<String> field = MockFactory.getDelayedCachedField(value, 2, stubSuccessListener)
        when:
        Single<String> single = Rx_extension_functionsKt.postToRx(field)
        String result = single.toBlocking().value()
        then:
        result == value
    }

    @Timeout(value = 1)
    def "should wait for a error"() {
        given:
        def ex = new MockException()
        Provider<String> failingProv = MockFactory.getFailingStringGetter(ex)
        CachedField<String> field = new CachedFieldImpl<String>(
                MockFactory.getSameSessionIdProvider(),
                failingProv,
                stubSuccessListener,
                stubErrorListener
        )
        when:
        Single<String> single = Rx_extension_functionsKt.postToRx(field)
        single.toBlocking().value()
        then:
        thrown MockException
    }

    @Timeout(value = 1)
    def "should return quickly if value is already loaded"() {
        given:
        def callCount = 0
        Provider<String> longSecondValueProv = {
            if (callCount == 0) {
                callCount++; return value
            } else {
                sleep 10E6; return value
            }
        }
        CachedField<String> field = MockFactory.getLoadedCachedField(longSecondValueProv)
        when:
        Single<String> single = Rx_extension_functionsKt.postToRx(field)
        String result = single.toBlocking().value()
        then:
        result == value
    }

    @Timeout(value = 2)
    def "should not wait for the value if we unsubscribe"() {
        given:
        long sleepTime = 10E6
        CachedField<String> field = MockFactory.getDelayedCachedField(value, sleepTime, stubSuccessListener)
        when:
        Single<String> single = Rx_extension_functionsKt.postToRx(field)
        def result = single.subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .test()
        result.unsubscribe()
        sleep 8
        then:
        result.onNextEvents.isEmpty()
        result.assertUnsubscribed()
    }

    @Timeout(value = 4)
    def "should deliver error if cached field cancels loading due to new arg"() {
        given:
        long sleepTime = 10E6
        ProviderWithArg<String, Integer> provider = MockFactory.getDelayedStringGetter(argToValueMap, sleepTime)
        CachedFieldWithArg<String, Integer> field = new CachedFieldWithArgImpl(
                MockFactory.getSameSessionIdProvider(),
                provider,
                MockFactory.getSuccessListenerWithArg(),
                MockFactory.getErrorListenerWithArg()
        )

        when:
        Single<String> single = Rx_extension_functionsKt.postToRx(field, 1)
        def result = single.subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .test()
        sleep 32
        field.postValue(2)
        result.awaitTerminalEvent()
        then:
        result.onNextEvents.isEmpty()
        result.assertError(InterruptedException.class)
        result.assertUnsubscribed()
    }

    @Timeout(value = 1)
    def "should call onError if null is returned from the provider"() {
        given:
        ProviderWithArg<String, Integer> provider = { null }
        CachedFieldWithArg<String, Integer> field = new CachedFieldWithArgImpl(
                MockFactory.getSameSessionIdProvider(),
                provider,
                MockFactory.getSuccessListenerWithArg(),
                MockFactory.getErrorListenerWithArg()
        )
        def testScheduler = Schedulers.test()

        when:
        Single<String> single = Rx_extension_functionsKt.postToRx(field, 1)
        def result = single.subscribeOn(testScheduler)
                .observeOn(testScheduler)
                .test()
        testScheduler.triggerActions()
        then:
        result.onNextEvents.isEmpty()
        !result.onErrorEvents.isEmpty()
    }
}


class MockException extends RuntimeException {}