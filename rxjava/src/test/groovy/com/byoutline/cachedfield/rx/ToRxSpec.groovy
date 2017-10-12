package com.byoutline.cachedfield.rx

import com.byoutline.cachedfield.*
import rx.Single
import rx.Subscription
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

    @Timeout(value = 1)
    def "should not wait for the value if we unsubscribe"() {
        given:
        long sleepTime = 10E6
        CachedField<String> field = MockFactory.getDelayedCachedField(value, sleepTime, stubSuccessListener)
        when:
        Single<String> single = Rx_extension_functionsKt.postToRx(field)
        def result = subscribeAndBlockUntilUnsubscribe(single, field, { Subscription subscription ->
            subscription.unsubscribe()
        })
        then:
        !result.onSuccessCalled
        !result.onErrorCalled
    }

    @Timeout(value = 1)
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
        def result = subscribeAndBlockUntilUnsubscribe(single, field, { field.postValue(2) })
        then:
        !result.onSuccessCalled
        result.onErrorCalled
    }

    static SingleResult subscribeAndBlockUntilUnsubscribe(Single<String> single, CachedField field, Closure actionAfterSubscribe) {
        subscribeAndBlockUntilUnsubscribe(single, field.toCachedFieldWithArg(), actionAfterSubscribe)
    }

    static SingleResult subscribeAndBlockUntilUnsubscribe(Single<String> single, CachedFieldWithArg field, Closure actionAfterSubscribe) {
        def subscribed = true
        def result = new SingleResult()
        def subscription = single
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .doOnUnsubscribe { subscribed = false }
                .doAfterTerminate { subscribed = false }
                .subscribe({ result.onSuccessCalled = true },
                { result.onErrorCalled = true })
        while (field.getState() != FieldState.CURRENTLY_LOADING) {
            sleep 1
        }
        actionAfterSubscribe(subscription)
        while (subscribed) {
            sleep 1
        }
        return result
    }
}

class SingleResult {
    def onSuccessCalled = false
    def onErrorCalled = false
}

class MockException extends RuntimeException {}