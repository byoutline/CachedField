package com.byoutline.cachedfield

import com.byoutline.cachedfield.CachedFieldImpl
import com.byoutline.eventcallback.ResponseEvent
import com.byoutline.eventcallback.ResponseEventImpl
import com.byoutline.eventcallback.internal.actions.AtomicBooleanSetter
import com.byoutline.eventcallback.internal.actions.CreateEvents
import com.byoutline.eventcallback.internal.actions.ResultEvents
import com.byoutline.eventcallback.internal.actions.ScheduledActions
import javax.inject.Provider
import com.google.gson.reflect.TypeToken
import spock.lang.Shared
import spock.lang.Unroll

/**
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 27.06.14.
 */
class CachedFieldSpec extends spock.lang.Specification {
    @Shared
    String value = "value"
    SuccessListener<String> stubSuccessListener = {} as SuccessListener<String>
    
    
    def "postValue should return immediately"() {
        given:
        CachedField field = MockFactory.getDelayedCachedField(value, 1000, stubSuccessListener)
        
        when:
        boolean tookToLong = false
        Thread.start {
            sleep 15
            tookToLong = true;
        }
        field.postValue()
        
        then:
        if(tookToLong) {
            throw new AssertionError("Test took to long to execute")
        }
    }

    
    def "should post success event immediately if it was loaded"() {
        given:
        CachedField field = MockFactory.getDelayedCachedField(value, 20, stubSuccessListener)
        field.postValue()
        waitUntilFieldLoads(field)
        
        when:
        boolean tookToLong = false
        Thread.start {
            sleep 15
            tookToLong = true;
        }
        field.postValue()
        
        then:
        if(tookToLong) {
            throw new AssertionError("Test took to long to execute")
        }
    }
    
    def "should inform listener when loaded"() {
        given:
        boolean valuePosted = false
        def successList = {valuePosted = true} as SuccessListener<String>
        CachedField field = MockFactory.getDelayedCachedField(value, successList)
        
        when:
        field.postValue()
        waitUntilFieldLoads(field)
        
        then:
        assert valuePosted
    }
    
    def "should null out value when drop is called"() {
        given:
        CachedField field = MockFactory.getLoadedCachedField(value)
        waitUntilFieldLoads(field)
        
        when:
        field.drop()
        
        then:
        field.getState() == FieldState.NOT_LOADED
        field.value.value == null
    }
    
    def waitUntilFieldLoads(CachedField field) {
        while(field.getState() != FieldState.LOADED) {
            sleep 1
        }
    }
}
