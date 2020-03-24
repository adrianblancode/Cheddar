package co.adrianblan.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/** Applies action to first emission */
fun <T> Flow<T>.onFirst(action: suspend (T) -> Unit): Flow<T> =
    flow {
        var count = 0

        collect { value ->
            if (count == 0) action(value)
            count++
            emit(value)
        }
    }