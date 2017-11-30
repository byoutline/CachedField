package com.byoutline.observablecachedfield

import android.databinding.Observable
import android.databinding.ObservableField
import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.cachedfield.ErrorListener
import com.byoutline.cachedfield.MockFactory
import com.byoutline.cachedfield.SuccessListener
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.byoutline.observablecachedfield.internal.NullArgumentException
import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class ObservableCachedFieldWithArgSpec extends Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']

    @Unroll
    def "should notify about new value: #val for arg: #arg"() {
        given:
        ObservableCachedFieldWithArg field = builder()
                .withValueProviderWithArg(MockFactory.getStringGetter(argToValueMap))
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .build()
        def callback = new MockObservableCallback()
        ObservableField<String> obs = field.observable()
        obs.addOnPropertyChangedCallback(callback)

        when:
        field.postValue(arg)

        then:
        callback.called
        obs.get() == val

        where:
        val | arg
        'a' | 1
        'b' | 2
    }

    @Unroll
    def "should set error value in observable"() {
        given:
        def field = builder
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .build()
        def callback = new MockObservableCallback()
        ObservableField<String> errObs = field.getObservableError()
        errObs.addOnPropertyChangedCallback(callback)
        when:
        post(field, 8)
        then:
        callback.called
        errObs.get() instanceof RuntimeException
        errObs.get().message == "E8"
        where:
        builder << [builder().withValueProviderWithArg(MockFactory.getFailingStringGetterWithArg()),
                    builder().withValueProvider(MockFactory.getFailingStringGetter(new RuntimeException("E8")))]
    }

    static void post(field, arg) {
        if(field instanceof ObservableCachedField) {
            field.postValue()
        } else {
            ((CachedFieldWithArg) field).postValue(arg)
        }
    }

    def "builder should not allow null value getter"() {
        when:
        builder()
                .withValueProvider(null)
                .build()
        then:
        thrown NullArgumentException
    }

    def "builder should not allow null state listener executor"() {
        when:
        builder()
                .withValueProvider(MockFactory.getStringGetter("value"))
                .withCustomStateListenerExecutor(null)
                .build()
        then:
        thrown NullArgumentException
    }

    @Unroll
    def "No arg version smoke test for value: #val"() {
        given:
        ObservableCachedField<String> field = new ObservableCachedField<String>(MockFactory.getSameSessionIdProvider(),
                MockFactory.getStringGetter(val),
                {} as SuccessListener<String>,
                {} as ErrorListener,
                MoreExecutors.newDirectExecutorService(),
                DefaultExecutors.createDefaultStateListenerExecutor()
        )
        boolean called = false
        def callback = new Observable.OnPropertyChangedCallback() {

            @Override
            void onPropertyChanged(Observable sender, int propertyId) {
                called = true
            }
        }
        ObservableField<String> obs = field.observable()
        obs.addOnPropertyChangedCallback(callback)

        when:
        field.postValue()

        then:
        called
        obs.get() == val

        where:
        val << ['a', 'b']
    }

    private static ObservableCachedFieldBuilder builder() {
        return new ObservableCachedFieldBuilder()
    }
}

class MockObservableCallback extends Observable.OnPropertyChangedCallback {
    public boolean called

    @Override
    void onPropertyChanged(Observable sender, int propertyId) {
        called = true
    }
}