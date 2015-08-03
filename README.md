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


#### Including in projects ####
Add as a dependency to your ```build.gradle```:
```groovy
compile 'com.byoutline.cachedfield:cachedfield:1.5.0'
```
