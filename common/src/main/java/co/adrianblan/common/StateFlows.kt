package co.adrianblan.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

// Takes a converts a Flow an initial value into a StateFlow
fun <T> Flow<T>.toStateFlow(initialValue: T): StateFlow<T> =
    StateFlowWrapper(this, initialValue)

private class StateFlowWrapper<T>(
    private val flow: Flow<T>,
    initialValue: T
) : AbstractFlow<T>(), StateFlow<T> {

    override var value: T = initialValue
        private set

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        flow.collect {
            value = it

            collector.emit(it)
        }
    }
}

/** Maps one StateFlow into another */
fun <T, V> StateFlow<T>.mapStateFlow(
    mapper: (T) -> V
): StateFlow<V> = StateFlowMapper(this, mapper)


private class StateFlowMapper<T, V>(
    private val stateFlow: StateFlow<T>,
    private val mapper: (T) -> V
) : AbstractFlow<V>(), StateFlow<V> {

    override val value: V
        get() = mapper(stateFlow.value)

    override suspend fun collectSafely(collector: FlowCollector<V>) {
        stateFlow.collect {
            collector.emit(mapper(it))
        }
    }
}

