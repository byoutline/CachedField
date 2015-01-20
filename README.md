CachedField
===========

Wrapper for expensive resources.

If you use ```Otto``` bus take a look at [OttoCachedField](https://github.com/byoutline/OttoCachedField)

#### Interface description ####
Each [Cached field](https://github.com/byoutline/CachedField/blob/master/src/main/java/com/byoutline/cachedfield/CachedField.java) have only 4 methods as its interface:
```java
void postValue();
```
which posts current value when it's ready - most often that happens immediately if the value was already calculated/fetched, or after time needed for it recalculation if session changed or it is first time that this value is requested.

```java
void refresh();
```
that forces recalucaltion of the value (and then posts event),

```java
FieldState getState();
```
that returns current state of the field (this is typically used to display some kind of progress indicator to user).

```java
void drop();
```
that orders cached value to be forgotten, so the memory can be reclaimed.

#### Including in projects ####
Add as a dependency to your ```build.gradle```:
```groovy
compile 'com.byoutline.cachedfield:cachedfield:1.3.1'
```

#### Latest changes ####
* 1.3.1 Added ability to pass ```FieldStateListener``` to constructor that will be informed each time CachedField state changes. That can be useful for displaying busy indicator in graphical applications.
* 1.3.0 Added method ```drop()``` that can be used to force clear a cached value. That can be used when fe: system runs low on memory.
