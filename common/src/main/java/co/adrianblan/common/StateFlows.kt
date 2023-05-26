package co.adrianblan.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InitialFlow<T> internal constructor(flow: Flow<T>, internal val initialValue: T) :
    Flow<T> by flow

fun <T> Flow<T>.withInitialValue(initialValue: T): InitialFlow<T> = InitialFlow(this, initialValue)

fun <T> InitialFlow<T>.toStateFlow(
    scope: CoroutineScope,
    sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(500)
): StateFlow<T> = this.stateIn(scope, sharingStarted, this.initialValue)

/**
 * Launches a coroutine under scope and collects the input StateFlow, emitting it to a output StateFlow.
 * This is useful if you want to back a cold short scoped StateFlow with a longer scoped hot one.
 */
fun <T> StateFlow<T>.collectAsStateFlow(scope: CoroutineScope): StateFlow<T> {

    val input: StateFlow<T> = this
    val output = MutableStateFlow<T>(input.value)

    scope.launch {
        input.collect {
            output.value = it
        }
    }

    return output
}

fun <T, M> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> M
): StateFlow<M> = map { mapper(it) }.stateIn(
    coroutineScope,
    SharingStarted.Eagerly,
    mapper(value)
)