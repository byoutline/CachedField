#### CachedField ####
* 1.7.0 Cached Field subrprojects moved to this repo and their release numbers were synchronized.
  * `SameSessionIdProvider` - Default implementation for `session id provider` that always return same session - Useful 
  for projects that do not use `session id` at all.
  * `CachedField` now can be converted to arg version via `toCachedFieldWithArg` method. This is intended
  for libraries to make it easier to handle different types of `CachedField` at once.
  * `CachedFieldWithArg` now can return complete state and value via `getStateAndValue` (which provides more thread safety)
  * `OttoObservableCachedField`, `IBusCachedField`, `EventBusCachedField` are now deprecated. 
     * `1.7.0-RC1` versions were released and have the changes from CachedField `1.7.0`, but there will probably no further feature development for them 
  * `ObservableCachedField` 
       * no longer depends on bus. 
       * `ObservableCachedFieldBuilder` added
       * `RetrofitHelper` copied from iBus
       * Now requires Java 7 
       * Kotlin dependency added
       * `registerChangeCallback` extension function added - now when bus is gone this is a helper
        function to do custom action when observables do change.
  
* 1.5.3 Revert change from 1.3.4 (now field state is again set before informing success listeners - 1.3.4 allowed for rare race conditions)
* 1.5.2 CachedFieldsListeners added to utils - allows listening to multiple CachedFields and CachedEndpoints at once
* 1.5.1 Lower method count of dependencies.
* 1.5.0 Added CachedEndpointWithArg to allow using ```CachedField``` like API for non ```GET``` calls.
* 1.4.1 Renamed inconsistently named of ```DbSaverWithArg``` to ```DbWriterWithArg```
* 1.4.0 
  * Added support for providing custom ```ExecutorService```/```Executor``` for value loading and state listener calls
  * Added DB Cache utils - You can use ```DbCachedValueProvider```/```DbCachedValueProviderWithArg``` to combine
  steps of fetching data from API, saving it to db, and loading it from DB. It allows later to decide whether you
  want to reload data from API or from DB by passing ```FetchType.API``` or ```FetchType.DB``` as argument to ```post```
  and ```reload```
 
* 1.3.4 Build script refactor. Should not change public API.
* 1.3.3 Java 1.6 compatibility
* 1.3.2 
  * Added ```CachedFieldWithArg``` that allows to pass argument to value Provider. 
  * Changed ```FieldStateListener``` api from requiring it in constructor to more traditional add/remove listener.
* 1.3.1 Added ability to pass ```FieldStateListener``` to constructor that will be informed each time CachedField state changes. 
That can be useful for displaying busy indicator in graphical applications.
* 1.3.0 Added method ```drop()``` that can be used to force clear a cached value. That can be used when fe: system runs low on memory.

#### OttoCachedField ####
* 1.6.3 RetrofitHelper added  - methods that allow easier use of CachedField for Retrofit 2.
* 1.6.2 No new features, call to bus that did nothing removed.
* 1.6.1 No new features, call to bus that did nothing removed.
* 1.6.0 MainThreadExecutor class added for convenience of Android projects.
* 1.5.2 No new features, new module with OttoObservableCachedField added with separate numeration.
* 1.5.1 OttoCachedEndpointWithArg constructor is now protected instead of package private, so users can extend this class to hide long generic types.
* 1.5.0 Added OttoCachedEndpoint to allow using CachedField like API for non GET calls ([read more](https://github.com/byoutline/CachedField#cachedendpoint))
* 1.4.0 
  * Added support for providing custom ```ExecutorService```/```Executor``` for value loading and state listener calls
  * Added DB Cache utils - You can use ```withApiFetcher```, ```withDbWriter```, ```withDbReader``` methods in builders to combine
  steps of fetching data from API, saving it to db, and loading it from DB. It allows to decide at runtime whether you
  want to reload data from API or from DB by passing ```FetchType.API``` or ```FetchType.DB``` as argument to ```post```
  and ```reload```
  * Changed builders to allow calling ```withCustomSessionIdProvider``` and ```withCustomBus``` in any order.
  
#### ObservableCachedField ####
* 1.1.0 universal `CachedFieldBuilder` added
* 1.0.1 Dependencies updated
* 1.0.0 Initial release (imported from KickMaterial)