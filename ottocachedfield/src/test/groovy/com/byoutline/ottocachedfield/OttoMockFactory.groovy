package com.byoutline.ottocachedfield

import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.ibuscachedfield.IBusCachedFieldWithArgBuilder
import com.squareup.otto.Bus

static IBusCachedFieldWithArgBuilder<String, Integer, Bus, CachedFieldWithArg<String, Integer>> ottoWithArgBuilder() {
    OttoCachedFieldWithArg.builder()
}

static List<IBusCachedFieldWithArgBuilder> busCachedFieldsWithArgBuilders() {
    [ottoWithArgBuilder()]
}