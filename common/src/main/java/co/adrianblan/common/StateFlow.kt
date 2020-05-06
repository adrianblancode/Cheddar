package co.adrianblan.common

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

// TODO remove when StateFlow is released in Kotlin

/** A StateFlow is a Flow which stores the last emitted value */
interface StateFlow<T> : Flow<T> {
    val value: T
}

fun <T> Flow<T>.asStateFlow(initialValue: T): StateFlow<T> =
    StateFlowWrapper(this.onStart { emit(initialValue) }, initialValue)

fun <T, V> StateFlow<T>.mapStateFlow(transform: (T) -> V): StateFlow<V> =
    StateFlowMapperImpl(this, transform)

class MutableStateFlow<T>
internal constructor(
    private val channel: ConflatedBroadcastChannel<T>
) : StateFlow<T>, Flow<T> by channel.asFlow() {

    constructor(initialValue: T) : this(ConflatedBroadcastChannel(initialValue))

    override val value: T get() = channel.value

    fun offer(value: T) = channel.offer(value)
}

private class StateFlowWrapper<T>(
    private val flow: Flow<T>,
    private val initialValue: T
)
    : AbstractFlow<T>(), StateFlow<T> {

    override var value: T = initialValue

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        flow.collect {
            value = it
            collector.emit(it)
        }
    }
}

private class StateFlowMapperImpl<T, V>(
    private val stateFlow: StateFlow<T>,
    private val transform: (T) -> V
) : StateFlow<V>, Flow<V> by stateFlow.mapSynchronous(transform) {
    override val value: V get() = transform(stateFlow.value)
}

// Regular map transform is a suspend fun, so let's make one for synchronous only
private fun <T, V> Flow<T>.mapSynchronous(transform: (T) -> V): Flow<V> =
    map { transform(it) }