# Retrofit 2 adapter

Only `ObservableCachedField<RETURN_TYPE>` is currently supported. 
Argument versions of CachedField do not seem possible to implement as and adapter. 

How to use
----------

Add `CachedFieldCallAdapterFactory` as a `Call` adapter when building your `Retrofit` instance:
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://example.com/")
    .addCallAdapterFactory(CachedFieldCallAdapterFactory.create())
    .build();
```

`CachedFieldCallAdapterFactory.create` does accept optional parameters.