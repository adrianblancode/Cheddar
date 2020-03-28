package co.adrianblan.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext

/** Wrapper to require a child scope from a parent scope, without exposing the parent scope */
class ParentScope
internal constructor(
    private val parentScope: CoroutineScope
) {
    fun createChildScope(): CoroutineScope {
        val parentJob = parentScope.coroutineContext[Job]!!
        return parentScope + SupervisorJob(parentJob)
    }
}

fun CoroutineScope.asParentScope() = ParentScope(this)