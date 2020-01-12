package co.adrianblan.network

data class ApiException(
    val statusCode: Int,
    val statusMessage: String,
    val errorResponse: String?
) : Exception("$statusCode $statusMessage $errorResponse")

/** Wrapper for Retrofit Response */
sealed class ApiResponse<out T> {

    abstract val statusCode: Int?

    abstract val statusMessage: String

    val isSuccessful: Boolean
        get() = statusCode in 200 until 300

    data class Success<out T>(
        override val statusCode: Int,
        override val statusMessage: String,
        val data: T
    ) : ApiResponse<T>() {

        // Returns true if empty response
        inline val isEmpty: Boolean
            get() = data == null

        @Suppress("UNCHECKED_CAST")
        internal fun <V> unsafeCast(): ApiResponse<V> = this as ApiResponse<V>

        // Creates a copy of the success, but with different data
        @Suppress("UNCHECKED_CAST")
        fun <V> withData(data: V): Success<V> {
            if (isEmpty && data == null) {
                return this as Success<V>
            }
            return (this as Success<V>).copy(data = data)
        }
    }

    data class Error<out T>(
        override val statusCode: Int?,
        override val statusMessage: String,
        val error: Throwable,
        val errorResponse: String?
    ) : ApiResponse<T>() {

        constructor(error: ApiException) : this(error.statusCode, error.statusMessage, error, error.errorResponse)

        init {
            require(!isSuccessful) { "Cannot create a successful Error" }
        }

        /**
         * Cast this class's generic to another type.
         */
        @Suppress("UNCHECKED_CAST") // Fine because this class does not refer to T
        fun <V> cast(): ApiResponse<V> = this as ApiResponse<V>

        companion object {

            @JvmStatic
            fun <T> createForNetworkError(error: Throwable): ApiResponse<T> =
                Error(null, requireNotNull(error.message), error, null)
        }
    }
}