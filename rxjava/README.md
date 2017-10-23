Kotlin Extension functions that wraps CachedField to return rxJava (1) `Single`.
Single does invoke `post` on subscription (or `call` for `CachedEndpointWithArg`).
 
 Example:
 ```kotlin
	field.postToRx().subscribe({Timber.v("Success: $it")}, {Timber.v("Error: $it")})
 ```