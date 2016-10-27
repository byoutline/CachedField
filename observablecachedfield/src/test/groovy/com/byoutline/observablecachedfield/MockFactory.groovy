package com.byoutline.observablecachedfield

import com.byoutline.cachedfield.ProviderWithArg

import javax.inject.Provider

static Provider<String> getSameSessionIdProvider() {
    return { return "sessionId" } as Provider<String>
}

static ProviderWithArg<String, Integer> getStringGetter(Map<Integer, String> argToValueMap) {
    return [get     : { Integer arg -> return argToValueMap.get(arg) },
            toString: { "string getter with arg: " + argToValueMap }
    ] as ProviderWithArg<String, Integer>
}

static ProviderWithArg<String, Integer> getFailingStringGetterWithArg() {
    return [get     : { Integer arg -> throw new RuntimeException("E" + arg) },
            toString: { "fail provider with arg" }] as ProviderWithArg<String, Integer>
}