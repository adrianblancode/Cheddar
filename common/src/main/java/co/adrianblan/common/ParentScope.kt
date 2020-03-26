package co.adrianblan.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext

class ParentScope
private constructor(
    private val parentScope: CoroutineScope
) {
    fun createChildScope(): CoroutineScope {
        val parentJob = parentScope.coroutineContext[Job]!!
        return parentScope + SupervisorJob(parentJob)
    }

    companion object {
        fun of(scope: CoroutineScope) =
            ParentScope(scope)
    }
}