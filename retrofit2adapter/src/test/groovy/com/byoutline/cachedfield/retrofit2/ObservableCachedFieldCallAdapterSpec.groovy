package com.byoutline.cachedfield.retrofit2

import com.byoutline.cachedfield.MockCachedFieldLoader
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.byoutline.observablecachedfield.ObservableCachedField
import com.google.common.util.concurrent.MoreExecutors
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import retrofit2.Retrofit
import retrofit2.http.GET
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AFTER_REQUEST

class ObservableCachedFieldCallAdapterSpec extends Specification {
    @Shared
    def value = "OK"
    @Rule
    public final MockWebServer server = new MockWebServer()
    interface Service {
        @GET('/')
        ObservableCachedField<String> observable()
    }

    Service service

    def setup() {
        def factory = CachedFieldCallAdapterFactory.create(MoreExecutors.newDirectExecutorService(),
                DefaultExecutors.createDefaultStateListenerExecutor())
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(new StringConverterFactory())
                .addCallAdapterFactory(factory)
                .build()
        service = retrofit.create(Service.class)
    }

    @Unroll
    def "#testName should produce value: #expVal, error body: #expContainErrorResponse and exception: #expContainException"() {
        given:
        server.enqueue(response)
        when:
        def field = service.observable()
        MockCachedFieldLoader.postAndWaitUntilFieldStopsLoading(field)
        def successVal = field.observable().get()
        def errorVal = field.getObservableError().get()
        then:
        successVal == expVal
        (errorVal?.errorResponse != null) == expContainErrorResponse
        (errorVal?.cause?.class != null) == expContainException
        where:
        testName      | response                                       | expVal | expContainErrorResponse | expContainException
        'success 200' | mr().setBody(value)                            | value  | false                   | false
        'success 404' | mr().setResponseCode(404)                      | null   | true                    | false
        'failure'     | mr().setSocketPolicy(DISCONNECT_AFTER_REQUEST) | null   | false                   | true
    }

    static MockResponse mr() {
        new MockResponse()
    }
}