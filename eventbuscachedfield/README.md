# EventBusCachedField
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.byoutline.eventbuscachedfield/eventbuscachedfield/badge.svg?style=flat)](http://mvnrepository.com/artifact/com.byoutline.eventbuscachedfield/eventbuscachedfield)
[![Coverage Status](https://coveralls.io/repos/byoutline/EventBusCachedField/badge.svg?branch=master)](https://coveralls.io/r/byoutline/EventBusCachedField?branch=master)
 master:  [![Build Status](https://travis-ci.org/byoutline/EventBusCachedField.svg?branch=master)](https://travis-ci.org/byoutline/EventBusCachedField)
 develop: [![Build Status](https://travis-ci.org/byoutline/EventBusCachedField.svg?branch=develop)](https://travis-ci.org/byoutline/EventBusCachedField)
 
Wrapper for expensive values (like API calls) that post results by greenrobot EventBus. Additionally it guards against displaying data from one user to another.

How to use
----------
##### Including dependency #####
Add to your ```build.gradle```:
```groovy
compile 'com.byoutline.eventbuscachedfield:eventbuscachedfield:0.9.2'
```

##### Init common settings #####
To avoid passing same values to each of your CachedFields put following into your code (typically to ```Application``` ```onCreate``` method).
```java
EventBusCachedField.init(sessionIdProvider, bus);
```
where bus is greenrobot ```EventBus``` bus instance, and sessionIdProvider is a ```Provider``` of current session. ```Provider``` is supposed to return same string as long as same user is logged in. Typically it something like authorization header for your API calls.

##### Declare your fields #####
To declare your field you should pass it a ```Provider```, that synchronously calculates/fetches your value. You also have to pass ```ResponseEvent``` that will be posted when value is ready, and optionally Event that will be posted in case of failure. 
```java
public final CachedField<YourExpensiveValue> expensiveValue = new EventBusCachedField<>(new Provider<YourExpensiveValue>() {
        @Override
        public YourExpensiveValue get() {
            return service.getValueFromApi();
        }
    }, new ValueFetchedEvent(), new ValueFetchFailedEvent());
```
```java
public class ValueFetchedEvent extends ResponseEventImpl<YourExpensiveValue> {
}
```

If you skipped init common settings step or want to override default project value for this specific field you may also pass sessionIdProvider and EventBus bus instance.

Note: It is advised to put your cached field in some sort of a manager or other object that is not connected to Android view lifecycle. It will allow you to keep your cached values between screen rotation, etc.

##### Get value when it's ready #####
```java
    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        manager.expensiveValue.postValue();
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }
    
    @Subscribe
    public void onValueFetched(ValueFetchedEvent event) {
        YourExpensiveValue value = event.getResponse();
        // do something with your value
    }
    @Subscribe
    public void onValueFetchFailedFailed(ValueFetchFailedEvent event) {
        // do something about failure
    }
```

Calling ```postValue``` or ```refresh``` will always cause CachedField to post either Success Event or Error Event.

### Interface description ###

See [Cached field](https://github.com/byoutline/CachedField#interface-description) to read more about basic methods like ```refresh```, ```drop``` etc.


### Parametric fields ###

In case your value depends on some argument  (for example API GET call that requires item ID) you can use [EventBusCachedFieldWithArg](https://github.com/byoutline/EventBusCachedField/blob/master/src/main/java/com/byoutline/eventbuscachedfield/EventBusCachedFieldWithArg.java) . It supports same methods but requires you to pass argument to ```post``` and ```refresh``` calls. Only one value will be cached at the time, so changing argument will force a refresh .

If you ask ```EventBusCachedFieldWithArg``` for value with new argument before last call had chance to finish, Success Event will be posted only about with value for current argument. Previous call will be assumed obsolete, and its return value(if any) will be discarded and Error Event will be posted instead.

If you want to check which call to field was completed check ```argValue``` parameter passed to your [ResponseEventWithArg](https://github.com/byoutline/IBusCachedField/blob/master/src/main/java/com/byoutline/ibuscachedfield/events/ResponseEventWithArg.java)


Prametric field classes have ```withArg``` suffix, and behave same as their no arg counterparts. Split exist only to enforce passing extra argument to methods that depend on it.

without arguments                              | with arguments
-----------------------------------------------|-----------------------------------------------
[EventBusCachedField](https://github.com/byoutline/EventBusCachedField/blob/master/src/main/java/com/byoutline/eventbuscachedfield/EventBusCachedField.java)  | [EventBusCachedFieldWithArg](https://github.com/byoutline/EventBusCachedField/blob/master/src/main/java/com/byoutline/eventbuscachedfield/EventBusCachedFieldWithArg.java)
[EventBusCachedFieldBuilder](https://github.com/byoutline/EventBusCachedField/blob/master/src/main/java/com/byoutline/eventbuscachedfield/EventBusCachedFieldBuilder.java)  | [EventBusCachedFieldWithArgBuilder](https://github.com/byoutline/EventBusCachedField/blob/master/src/main/java/com/byoutline/eventbuscachedfield/EventBusCachedFieldWithArgBuilder.java)
[ResponseEvent](https://github.com/byoutline/EventCallback/blob/master/src/main/java/com/byoutline/eventcallback/ResponseEvent.java) | [ResponseEventWithArg](https://github.com/byoutline/IBusCachedField/blob/master/src/main/java/com/byoutline/ibuscachedfield/events/ResponseEventWithArg.java)
[ResponseEventImpl](https://github.com/byoutline/EventCallback/blob/master/src/main/java/com/byoutline/eventcallback/ResponseEventImpl.java) | [ResponseEventWithArgImpl](https://github.com/byoutline/IBusCachedField/blob/master/src/main/java/com/byoutline/ibuscachedfield/events/ResponseEventWithArgImpl.java)
[Provider](https://docs.oracle.com/javaee/7/api/javax/inject/Provider.html) | [ProviderWithArg](https://github.com/byoutline/CachedField/blob/master/src/main/java/com/byoutline/cachedfield/ProviderWithArg.java)


### Builder syntax for EventBusCachedField instance creation ###
You may choose use ```builder``` instead of constructor to create your fields:
```java
new EventBusCachedFieldBuilder<>()
    .withValueProvider(new Provider<YourExpensiveValue>() {
        @Override
        public YourExpensiveValue get() {
            return service.getValueFromApi();
        }
    }).withSuccessEvent(new ValueFetchedEvent())
    .withResponseErrorEvent(new ValueFetchFailedEvent())
    .build();
```
Builder syntax is slightly longer, but makes it obvious which argument does what, and allows for better IDE autocompletion.


Not an EventBus user?
---------------------
If you do not want to use EventBus check [CachedField](https://github.com/byoutline/CachedField) project.
