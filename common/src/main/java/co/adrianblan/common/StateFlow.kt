package co.adrianblan.common

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow


/** A StateFlow is a Flow which stores the last emitted value */
interface StateFlow<T> : Flow<T> {
    val value: T
}

// TODO remove when Flow releases their StateFlow preview
internal class StateFlowImpl<T>
private constructor(
    private val innerChannel: ConflatedBroadcastChannel<T>,
    private val innerFlow: Flow<T> = innerChannel.asFlow()
) : StateFlow<T>, Flow<T> by innerFlow {

    override val value: T get() = innerChannel.value

    companion object {
        internal fun <T> of(channel: ConflatedBroadcastChannel<T>): StateFlow<T> =
            StateFlowImpl(channel)
    }
}

fun <T> ConflatedBroadcastChannel<T>.asStateFlow(): StateFlow<T> =
    StateFlowImpl.of(this)