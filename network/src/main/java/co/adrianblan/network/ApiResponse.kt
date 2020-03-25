package co.adrianblan.network

import retrofit2.Response

/** Api class which wraps network responses */
sealed class ApiResponse<T : Any?> {

    abstract val statusCode: Int

    data class Success<T>(
        override val statusCode: Int,
        // If empty response, data is null
        val data: T
    ) : ApiResponse<T>()

    data class ApiError<T>(
        override val statusCode: Int,
        val errorMessage: String?,
        val httpErrorMessage: String?
    ) : ApiResponse<T>()
}

fun <T : Any> Response<T>.wrapApiResponse(): ApiResponse<T?> {

    val response = this

    return if (response.isSuccessful) {

        val body = response.body()
            .takeIf { response.code() != 204 }

        ApiResponse.Success(
            statusCode = response.code(),
            data = body
        )
    } else {

        val errorMessage: String? =
            response.errorBody()?.string()

        ApiResponse.ApiError(
            statusCode = response.code(),
            errorMessage = errorMessage,
            httpErrorMessage = response.message()
        )
    }
}

// Unwraps api response to success value or throws
fun <T> ApiResponse<T>.unwrapApiResponse(): T =
    when (this) {
        is ApiResponse.Success -> this.data
        is ApiResponse.ApiError -> throw ApiException.of(this)
    }

// Maps a empty body response to list
fun <T : Any> ApiResponse<List<T>?>.mapNullResponseToEmptyList(): ApiResponse<List<T>> =
    this.mapNullResponseTo { emptyList() }

/** */
@Suppress("UNCHECKED_CAST")
fun <T : Any> ApiResponse<T?>.mapNullResponseTo(mapper: (ApiResponse<T?>) -> T): ApiResponse<T> =
    when (this) {
        is ApiResponse.Success<T?> ->
            ApiResponse.Success<T>(
                statusCode = statusCode,
                data = this.data ?: mapper(this)
            )
        is ApiResponse.ApiError<T?> -> this as ApiResponse.ApiError<T>
    }

// Converts an api response to nullable success, or throws otherwise
fun <T : Any> ApiResponse<T?>.toNullableSuccessResponseOrThrow(): ApiResponse.Success<T?> =
    when (this) {
        is ApiResponse.Success<T?> -> this
        is ApiResponse.ApiError -> throw ApiException.of(this)
    }

// Converts a successful api response to non null data, or throws otherwise
fun <T : Any> ApiResponse<T?>.throwIfEmptyResponse(): ApiResponse.Success<T> =
    when (this) {
        is ApiResponse.Success<T?> -> {
            if (data == null) throw EmptyResponseException()
            else ApiResponse.Success<T>(statusCode = statusCode, data = data)
        }
        is ApiResponse.ApiError -> throw ApiException.of(this)
    }


class EmptyResponseException() : Exception("Response returned empty body")

