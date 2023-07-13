package co.adrianblan.common

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*

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

/**
 * Takes in a flow that returns pages of values, and emits a flow with the sorted latest emission per page
 *
 * Eg [1, [A]], [2, [B]], [1, [AA]] emits [A], [A, B], [AA, B]
 */
fun <T> Flow<Pair<Int, List<T>>>.scanReducePages(): Flow<List<T>> =
    flow {

        val map = mutableMapOf<Int, List<T>>()

        coroutineScope {
            collect { (pageIndex: Int, value: List<T>) ->
                ensureActive()

                map[pageIndex] = value

                val sortedPages: List<T> =
                    map.entries
                        .sortedBy { it.key }
                        .map { it.value }
                        .flatten()

                emit(sortedPages)
            }
        }
    }
