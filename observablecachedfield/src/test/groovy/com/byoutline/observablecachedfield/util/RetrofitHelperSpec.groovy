package com.byoutline.observablecachedfield.util

import com.byoutline.cachedfield.ProviderWithArg
import com.byoutline.eventcallback.IBus
import com.byoutline.eventcallback.ResponseEvent
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.mock.Calls
import spock.lang.Shared
import spock.lang.Specification

import javax.inject.Provider

class RetrofitHelperSpec extends Specification {
    @Shared
    String value = 'value'
    @Shared
    def errorBody = ResponseBody.create(MediaType.parse('text'), 'error')
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']

    @Shared
    Exception exception = new RuntimeException('Cached Field test exception')
    ResponseEvent<String> successEvent
    ResponseEvent<Exception> errorEvent
    IBus bus

    def setup() {
        bus = Mock()
        successEvent = Mock()
        errorEvent = Mock()
    }

    def "apiValueProv should return call success as value NO ARGS"() {
        given:
        Provider<Call<String>> callProv = { Calls.response(value) }

        when:
        def result = RetrofitHelper.apiValueProv(callProv)

        then:
        result.get() == value
    }

    def "apiValueProv should return call success as value WITH ARGS"() {
        given:
        ProviderWithArg<Call<String>, Integer> callProv = { arg -> Calls.response(argToValueMap.get(arg)) }

        when:
        def result = RetrofitHelper.apiValueProv(callProv)

        then:
        result.get(1) == 'a'
        result.get(2) == 'b'
    }

    def "apiValueProv should throw Exception on failure NO ARGS"() {
        given:
        Provider<Call<String>> callProv = { Calls.response(Response.error(404, errorBody)) }

        when:
        def result = RetrofitHelper.apiValueProv(callProv)
        result.get()

        then:
        RetrofitHelper.ApiException ex = thrown()
        ex.errorResponse.errorBody() == errorBody
    }

    def "apiValueProv should throw Exception on failure WITH ARGS"() {
        given:
        ProviderWithArg<Call<String>, Integer> callProv = { arg -> Calls.response(Response.error(404, errorBody)) }

        when:
        def result = RetrofitHelper.apiValueProv(callProv)
        result.get()

        then:
        RetrofitHelper.ApiException ex = thrown()
        ex.errorResponse.errorBody() == errorBody
    }

    def "apiValueProv should propagate exception NO ARGS"() {
        given:
        Provider<Call<String>> callProv = { Calls.failure(new IOException()) }

        when:
        def result = RetrofitHelper.apiValueProv(callProv)
        result.get()

        then:
        RetrofitHelper.ApiException ex = thrown()
        ex.cause instanceof IOException
    }

    def "apiValueProv should propagate exception WITH ARGS"() {
        given:
        ProviderWithArg<Call<String>, Integer> callProv = { arg -> Calls.failure(new IOException()) }

        when:
        def result = RetrofitHelper.apiValueProv(callProv)
        result.get()

        then:
        RetrofitHelper.ApiException ex = thrown()
        ex.cause instanceof IOException
    }
}
