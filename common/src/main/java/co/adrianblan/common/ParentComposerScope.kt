package co.adrianblan.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

class ParentScope
private constructor(
    private val parentJob: Job
) {
    fun createChildScope(dispatcherProvider: DispatcherProvider) =
        CoroutineScope(SupervisorJob(parentJob) + dispatcherProvider.Main)

    companion object {
        fun of(scope: CoroutineScope) =
            ParentScope(scope.coroutineContext[Job]!!)
    }
}