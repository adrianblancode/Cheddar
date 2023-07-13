package co.adrianblan.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

val WhileSubscribed = SharingStarted.WhileSubscribed(500)

class InitialFlow<T> internal constructor(flow: Flow<T>, internal val initialValue: T) :
    Flow<T> by flow

fun <T> Flow<T>.withInitialValue(initialValue: T): InitialFlow<T> = InitialFlow(this, initialValue)

fun <T> InitialFlow<T>.toStateFlow(
    scope: CoroutineScope,
    sharingStarted: SharingStarted = WhileSubscribed
): StateFlow<T> = this.stateIn(scope, sharingStarted, this.initialValue)

fun <T, M> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> M
): StateFlow<M> = map { mapper(it) }.stateIn(
    coroutineScope,
    WhileSubscribed,
    mapper(value)
)