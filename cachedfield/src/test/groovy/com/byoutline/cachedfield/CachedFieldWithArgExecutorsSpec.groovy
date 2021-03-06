package com.byoutline.cachedfield

import com.byoutline.cachedfield.internal.DefaultExecutors
import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedFieldWithArgExecutorsSpec extends Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']

    def "should use passed executor for loading data"() {
        given:
        boolean called = false
        ExecutorService executor = [
                submit: { called = true; return new FutureTask((Runnable) it, null) }
        ] as ExecutorService
        CachedFieldWithArg field = CFMockFactory.getCachedFieldWithArg(argToValueMap, executor)

        when:
        field.postValue(1)

        then:
        called
    }

    def "should use passed executor for state listener"() {
        given:
        boolean called = false
        Executor stateListenersExecutor = { called = true; it.run() } as Executor
        ExecutorService loadExecutorService = MoreExecutors.newDirectExecutorService()
        CachedFieldWithArg field = new CachedFieldWithArgImpl(
                CFMockFactory.getSameSessionIdProvider(),
                CFMockFactory.getStringIntGetter(argToValueMap),
                CFMockFactory.getSuccessListenerWithArg(),
                CFMockFactory.getErrorListenerWithArg(),
                loadExecutorService,
                stateListenersExecutor
        )

        when:
        field.postValue(1)

        then:
        called
    }

    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
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
                CFMockFactory.getSameSessionIdProvider(),
                valueGetter,
                CFMockFactory.getSuccessListenerWithArg(),
                CFMockFactory.getErrorListenerWithArg(),
                CFMockFactory.getAsyncFirstTaskSyncOtherExecutorService(),
                DefaultExecutors.createDefaultStateListenerExecutor()
        )

        when:
        // Execute long running task asynchronously to be interrupted.
        field.postValue(10000)
        field.postValue(1)
        // Give some (minimal) time to propagate Thread.interrupt, since we
        // are running this post synchronously.
        while (!valueLoadingInterrupted) {
            sleep 1
        }

        then:
        valueLoadingInterrupted
        // Or Fail by timeout
    }
}
