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