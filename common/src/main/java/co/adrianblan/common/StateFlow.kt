package co.adrianblan.common

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow


/** A StateFlow is a Flow which stores the last emitted value */
interface StateFlow<T> : Flow<T> {
    val value: T?
}

// TODO remove when Flow releases their StateFlow preview
class StateFlowImpl<T>
internal constructor(
    private val innerChannel: ConflatedBroadcastChannel<T>,
    private val innerFlow: Flow<T> = innerChannel.asFlow()
) : StateFlow<T>, Flow<T> by innerFlow {

    override val value: T get() = innerChannel.value
}

fun <T> ConflatedBroadcastChannel<T>.asStateFlow(): StateFlow<T> =
    StateFlowImpl(this, this.asFlow())