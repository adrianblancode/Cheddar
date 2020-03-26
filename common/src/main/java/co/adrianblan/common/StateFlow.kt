package co.adrianblan.common

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow


/** A StateFlow is a Flow which stores the last emitted value */
interface StateFlow<T> : Flow<T> {
    val value: T?
}

// TODO remove when Flow releases their StateFlow preview
/**
 * Implementation delegates to inner flow
 */
class StateFlowImpl<T>
internal constructor(
    private val innerFlow: Flow<T>,
    private val initial: T?
) : StateFlow<T>, Flow<T> by innerFlow {
    override var value: T? = initial
        private set

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>) {

        val innerCollector =
            object : FlowCollector<T> {
                override suspend fun emit(value: T) {
                    this@StateFlowImpl.value = value
                    collector.emit(value)
                }
            }

        collect(innerCollector)
    }
}

fun <T> ConflatedBroadcastChannel<T>.asStateFlow(): StateFlow<T> =
    StateFlowImpl(this.asFlow(), this.value)

fun <T> Flow<T>.asStateFlow(): StateFlow<T> =
    StateFlowImpl(this, null)