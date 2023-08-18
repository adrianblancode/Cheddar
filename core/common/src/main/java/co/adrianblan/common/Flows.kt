package co.adrianblan.common

import kotlinx.coroutines.CancellationException
import kotlin.Result


suspend fun <R> suspendRunCatching(block: suspend () -> R): Result<R> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (t: Exception) {
        Result.failure(t)
    }