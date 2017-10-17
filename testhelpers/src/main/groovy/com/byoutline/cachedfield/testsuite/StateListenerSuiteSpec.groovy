package com.byoutline.cachedfield.testsuite

import com.byoutline.cachedfield.CachedField
import spock.lang.Specification

abstract class StateListenerSuiteSpec extends Specification {
    abstract def getField()

    abstract def waitUntilFieldFinishAction(field)

    def "should allow self removing state listeners"() {
        given:
        CachedField field = getField()
        def stateListeners = [new SelfRemovingFieldStateListener(field),
                              new SelfRemovingFieldStateListener(field),
                              new SelfRemovingFieldStateListener(field)]
        stateListeners.each { field.addStateListener(it) }
        when:
        field.postValue()
        waitUntilFieldFinishAction(field)
        then:
        stateListeners.findAll { it.called }.size() == 3
    }
}