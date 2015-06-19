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
}
