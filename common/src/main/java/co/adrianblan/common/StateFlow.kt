package co.adrianblan.common

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map

// TODO remove when StateFlow is released in Kotlin

/** A StateFlow is a Flow which stores the last emitted value */
interface StateFlow<T> : Flow<T> {
    val value: T
}

class MutableStateFlow<T>
private constructor(
    private val channel: ConflatedBroadcastChannel<T>
) : StateFlow<T>, Flow<T> by channel.asFlow() {

    constructor(initialValue: T) : this(ConflatedBroadcastChannel(initialValue))

    override val value: T get() = channel.value

    fun offer(value: T) = channel.offer(value)
}

class StateFlowMapperImpl<T, V>(
    private val stateFlow: StateFlow<T>,
    private val transform: (T) -> V
) : StateFlow<V>, Flow<V> by stateFlow.mapSynchronous(transform) {
    override val value: V get() = transform(stateFlow.value)
}

fun <T, V> StateFlow<T>.mapStateFlow(transform: (T) -> V): StateFlow<V> =
    StateFlowMapperImpl(this, transform)

// Regular map transform is a suspend fun, so let's make one for synchronous only
private fun <T, V> Flow<T>.mapSynchronous(transform: (T) -> V): Flow<V> =
    map { transform(it) }