package co.adrianblan.network

/** Exception thrown on api error */
class ApiException(
    val statusCode: Int,
    val httpErrorMessage: String?,
    val errorResponse: String?
) : Exception("$statusCode $httpErrorMessage $errorResponse") {

    companion object {
        // Creates an api exception from an error response
        fun <T> of(apiResponse: ApiResponse.ApiError<T>): ApiException =
            ApiException(
                statusCode = apiResponse.statusCode,
                httpErrorMessage = apiResponse.httpErrorMessage,
                errorResponse = apiResponse.errorMessage
            )
    }
}