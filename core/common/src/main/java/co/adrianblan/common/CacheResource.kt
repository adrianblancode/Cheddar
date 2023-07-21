package co.adrianblan.common

sealed class Resource<T> {
    class Loading<T>: Resource<T>()
    data class Success<T>(val value: T): Resource<T>()
}

fun <T : Any> T.toResource(): Resource<T> = Resource.Success<T>(this)