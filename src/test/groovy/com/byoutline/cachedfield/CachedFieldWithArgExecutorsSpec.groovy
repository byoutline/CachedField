package com.byoutline.cachedfield

import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Shared

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedFieldWithArgExecutorsSpec extends spock.lang.Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']

    def "should use passed executor for loading data"() {
        given:
        boolean called = false
        ExecutorService executor = [
                submit: { called = true; return new FutureTask((Runnable) it, null); }
        ] as ExecutorService
        CachedFieldWithArg field = MockFactory.getCachedFieldWithArg(argToValueMap, executor)

        when:
        field.postValue(1)

        then:
        called
    }

    def "should use passed executor for state listener"() {
        given:
        boolean called = false
        Executor stateListenersExecutor = { called = true } as Executor
        ExecutorService loadExecutorService = MoreExecutors.newDirectExecutorService()
        CachedFieldWithArg field = new CachedFieldWithArgImpl(
                MockFactory.getSameSessionIdProvider(),
                MockFactory.getStringIntGetter(argToValueMap),
                MockFactory.getSuccessListenerWithArg(),
                MockFactory.getErrorListenerWithArg(),
                loadExecutorService,
                stateListenersExecutor
        )

        when:
        field.postValue(1)

        then:
        called
    }

    def "should interrupt valueGetter thread"() {
        given:
        boolean valueLoadingInterrupted = false
        def valueGetter = { key ->
            try {
                Thread.sleep((long) key)
            } catch (InterruptedException ex) {
                valueLoadingInterrupted = true
            }
            return key
        } as ProviderWithArg<String, Integer>

        CachedFieldWithArg field = new CachedFieldWithArgImpl(
                MockFactory.getSameSessionIdProvider(),
                valueGetter,
                MockFactory.getSuccessListenerWithArg(),
                MockFactory.getErrorListenerWithArg(),
                MockFactory.getAsyncFirstTaskSyncOtherExecutorService(),
                null
        )

        when:
        field.postValue(10000)
        field.postValue(0)

        then:
        valueLoadingInterrupted
        field.getState() == FieldState.LOADED
//        thrown(InterruptedException)
    }
}
