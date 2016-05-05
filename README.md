CachedField
===========
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.byoutline.cachedfield/cachedfield/badge.svg?style=flat)](http://mvnrepository.com/artifact/com.byoutline.cachedfield/cachedfield)
[![Coverage Status](https://coveralls.io/repos/byoutline/CachedField/badge.svg?branch=master)](https://coveralls.io/r/byoutline/CachedField?branch=master)
 master:  [![Build Status](https://travis-ci.org/byoutline/CachedField.svg?branch=master)](https://travis-ci.org/byoutline/CachedField)
 develop: [![Build Status](https://travis-ci.org/byoutline/CachedField.svg?branch=develop)](https://travis-ci.org/byoutline/CachedField)

Wrapper for expensive resources.

If you use ```Otto``` bus take a look at [OttoCachedField](https://github.com/byoutline/OttoCachedField)

#### Interface description ####
Each [Cached field](https://github.com/byoutline/CachedField/blob/master/src/main/java/com/byoutline/cachedfield/CachedField.java) supports following methods:
```java
void postValue();
```
which posts current value when it's ready - most often that happens immediately if the value was already calculated/fetched, 
or after time needed for it recalculation if session changed or it is first time that this value is requested.

```java
void refresh();
```
that forces recalculation of the value (and then posts event),

```java
FieldState getState();
```
that returns current state of the field (this is typically used to display some kind of progress indicator to user).

```java
void drop();
```
that orders cached value to be forgotten, so the memory can be reclaimed.

```java
void addStateListener(FieldStateListener listener);
boolean removeStateListener(FieldStateListener listener);
```
Allows adding and removing listeners that will be informed about 
[FieldState](https://github.com/byoutline/CachedField/blob/master/src/main/java/com/byoutline/cachedfield/FieldState.java) 
changes(like starting to load value). This can be useful for displaying busy indicator in graphical applications.

#### Parametric fields ####

In case your value depends on some argument  (for example API GET call that requires item ID) you can use 
[CachedFieldWithArg](https://github.com/byoutline/CachedField/blob/master/src/main/java/com/byoutline/cachedfield/CachedFieldWithArg.java). 
It supports same methods but requires you to pass argument to ```post``` and ```refresh``` calls. 
Only one value will be cached at the time, so changing argument will force a refresh. 

If you ask ```CachedFieldWithArg``` for value with new argument before last call had chance to finish,
[SuccessListenerWithArg](https://github.com/byoutline/CachedField/blob/master/src/main/java/com/byoutline/cachedfield/SuccessListenerWithArg.java) 
will be informed only about values with current argument. Previous call will be assumed obsolete, 
and its return value(if any) will be discarded and 
[ErrorListenerWithArg](https://github.com/byoutline/CachedField/blob/master/src/main/java/com/byoutline/cachedfield/ErrorListenerWithArg.java) 
(if any) will be called instead.


#### Controlling execution threads ####
 By default loading of value is executed asynchronously on background thread, and listeners are called
without thread switching. This means that by default you can safely invoke ```CachedField``` methods on UI
thread without blocking it(with possible exception of your state lister blocking it during ```drop``` call).

If you prefer to have more control over Threads on which value loading (or calling state listeners) is executed
```CachedFieldImpl``` accepts ```ExecutorService``` and ```Executor``` as arguments in constructor.

#### CachedEndpoint ####
If you have API calls that do not fit into ```CachedField``` (most of non GET calls) you may prefer to use
```CachedEndpoint``` instead. 

```CachedEndpoint``` differences compared to```CachedField```(other than different names and packages):
  * ```call()``` will always make an API call 
  (even if value was already loaded with same arguments - like ```refresh()```)
  * After failed call value is not dropped, and instead ```Exception``` is cached.
  * ```EndpointState``` compared to ```FieldState``` have additional state ```CALL_FAILED```
  * Instead of separate ```ErrorListener``` and ```SuccessListener``` there is only one ```CallEndListener```
  * Cached value can be read directly by ```getStateAndValue()```
  * Adding ```EndpointStateListener``` results in it being informed about current state and value
  
Some of the ```CachedEndpoint``` specification is still not decided (mostly behaviour with multiple concurrent calls
  to one endpoint). Therefore it should be considered beta feature, as the behaviour  may be adjusted in the future.

#### Session Id ####
Passing a provider of session id to CachedField allows it to check if cached value is still valid. For example
it will ensure that when user switches accounts data from old account will never be shown (even if it arrives after
account switch). Depending on application either session token, user name, or even empty string may be valid for this check.
As `sessionProvider` is standard [Provider](https://docs.oracle.com/javaee/7/api/javax/inject/Provider.html) you
may use Dependency Injection like [Dagger](https://google.github.io/dagger/) to create it ([example](https://github.com/byoutline/kickmaterial/blob/ee314bff89335c7186df56f0b9fe578b81e7b6d6/app/src/main/java/com/byoutline/kickmaterial/dagger/AppModule.java))
By default `sessionProvider` must be passed to `CachedFieldImpl` constructor, but some libraries like [OttoCachedField](https://github.com/byoutline/OttoCachedField)
allow to set default provider for whole project.
 
#### Testing with Espresso on Android ####
For instrumented tests on Android you may want to register your Cached Fields/Endpoints as 
[IdlingResources](https://developer.android.com/reference/android/support/test/espresso/IdlingResource.html).
Easiest way to do that is creating with `CachedFieldIdlingResource.from` method that takes any amount of 
Cached Fields/Endpoints and notifies Espresso when any of them is working. 
For example your test set up may look like this:

```java
private CachedFieldIdlingResource cachedFieldIdlingResource;

@Before
public void registerIdlingResources() {
    cachedFieldIdlingResource = CachedFieldIdlingResource.from(field1, field2, field3);
    Espresso.registerIdlingResources(cachedFieldIdlingResource);
}

@After
public void unregisterIdlingResources() {
    Espresso.unregisterIdlingResources(cachedFieldIdlingResource);
}
```

#### Including in projects ####
Add as a dependency to your ```build.gradle```:
```groovy
compile 'com.byoutline.cachedfield:cachedfield:1.5.3'
```
