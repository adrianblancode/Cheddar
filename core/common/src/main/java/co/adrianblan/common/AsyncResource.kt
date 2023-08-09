package co.adrianblan.common

class AsyncResource<T>(
    val cached: T?,
    val fetch: suspend () -> T
)