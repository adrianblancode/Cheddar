package co.adrianblan.common

/**
 * A generic class that holds a value with a loading / error status.
 * @param <T>
 */
sealed class Result<T : Any?> {

    open val successData: T? get() = (this as? Success)?.data

    // The last valid data, is included in both Loading and Error
    abstract val lastData: T?

    data class Loading<T>(override val lastData: T? = null) : Result<T>()

    data class Success<T>(val data: T) : Result<T>() {
        // Use the current data as last data
        override val lastData: T get() = data
        override val successData: T get() = data
    }

    data class Error<T>(val t: Throwable, override val lastData: T? = null) : Result<T>()

    @SuppressWarnings("UNCHECKED_CAST")
    fun <R> map(mapper: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(mapper(data))
            is Loading -> Loading(lastData?.let(mapper))
            is Error -> Error(t, lastData?.let(mapper))
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data"
            is Loading -> "Loading[lastData=$lastData]"
            is Error -> "Error[exception=$t, lastData=$lastData]"
        }
    }
}

val Result<*>.isSuccess
    get() = this is Result.Success && data != null

val Result<*>.isError
    get() = this is Result.Error

val Result<*>.isLoading
    get() = this is Result.Loading
