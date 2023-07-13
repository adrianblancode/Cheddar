package co.adrianblan.matryoshka.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus

internal fun CoroutineScope.invokeOnCompletion(block: () -> Unit) =
    coroutineContext[Job]!!
        .invokeOnCompletion {
            block()
        }

internal fun CoroutineScope.createChildScope(): CoroutineScope {
    val job = this.coroutineContext[Job]!!
    return this + SupervisorJob(job)
}