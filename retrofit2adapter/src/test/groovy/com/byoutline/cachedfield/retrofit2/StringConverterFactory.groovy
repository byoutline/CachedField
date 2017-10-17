package com.byoutline.cachedfield.retrofit2

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

import java.lang.annotation.Annotation
import java.lang.reflect.Type

final class StringConverterFactory extends Converter.Factory {
    @Override
    Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                     Retrofit retrofit) {
        return { ResponseBody value -> value.string() } as Converter<ResponseBody, String>
    }

    @Override
    Converter<?, RequestBody> requestBodyConverter(Type type,
                                                   Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return { String value -> RequestBody.create(MediaType.parse("text/plain"), value) } as Converter<String, RequestBody>
    }
}