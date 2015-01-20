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
        MockFactory.waitUntilFieldLoads(field)
        
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
    
    def "should inform success listener when loaded"() {
        given:
        boolean valuePosted = false
        def successList = {valuePosted = true} as SuccessListener<String>
        CachedField field = MockFactory.getDelayedCachedField(value, successList)
        
        when:
        field.postValue()
        MockFactory.waitUntilFieldLoads(field)
        
        then:
        assert valuePosted
    }
    
    def "should null out value when drop is called"() {
        given:
        CachedField field = MockFactory.getLoadedCachedField(value)
        
        when:
        field.drop()
        
        then:
        field.getState() == FieldState.NOT_LOADED
        field.value.value == null
    }
    
    def "should inform field state listener about changes on postValue"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        CachedField field = MockFactory.getDelayedCachedField(value, stateList)
        
        when:
        field.postValue()
        MockFactory.waitUntilFieldLoads(field)
        
        then:
        postedStates == [FieldState.CURRENTLY_LOADING, FieldState.LOADED]
    }
    
     def "should inform field state listener about changes on drop"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        CachedField field = MockFactory.getLoadedCachedField(value, stateList)
        postedStates.clear()
        
        when:
        field.drop()
        
        then:
        postedStates == [FieldState.NOT_LOADED]
    }
    
    
    def "should inform field state listener about changes on refresh"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        CachedField field = MockFactory.getLoadedCachedField(value, stateList)
        postedStates.clear()
        
        when:
        field.refresh()
        MockFactory.waitUntilFieldLoads(field)
        
        then:
        postedStates == [FieldState.CURRENTLY_LOADING, FieldState.LOADED]
    }
    
    def "should inform field state listener about changes on session expire"() {
        given:
        def postedStates = []
        def stateList = { FieldState newState -> postedStates.add(newState) } as FieldStateListener
        def currentSession = "one"
        def sessionProvider = { return currentSession } as Provider<String>
        CachedField field = MockFactory.getLoadedCachedField(value, stateList, sessionProvider)
        postedStates.clear()
        
        when:
        // Asking for state will force CachedField to check its current state
        // without us forcing it to change. This way only expired session can
        // cause state change.
        currentSession = "two"
        field.getState()
        
        then:
        postedStates == [FieldState.NOT_LOADED]
    }
}
