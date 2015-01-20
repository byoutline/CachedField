CachedField
===========

Wrapper for expensive resources.

If you use ```Otto``` bus take a look at [OttoCachedField](https://github.com/byoutline/OttoCachedField)

#### Interface description ####
Each [Cached field](https://github.com/byoutline/CachedField/blob/master/src/main/java/com/byoutline/cachedfield/CachedField.java) have (at the moment) only 3 methods as its interface:
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

Adding extra method ```void drop();``` is currently under consideration.
