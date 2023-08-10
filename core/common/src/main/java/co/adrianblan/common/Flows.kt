package co.adrianblan.common

import kotlinx.coroutines.CancellationException
import kotlin.Result


fun <R> runCatchingCooperative(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) {
            throw t
        }
        Result.failure(t)
    }
}