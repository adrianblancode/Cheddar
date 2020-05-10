package co.adrianblan.matryoshka

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus

interface NodeContext {
    val nodeScope: CoroutineScope

    companion object {
        /**
         * Creates a root NodeContext.
         *
         * Cancelling the root scope cancels all the work in the node tree,
         * but does not detach any nodes.
         */
        fun createRoot(rootScope: CoroutineScope) =
            NodeContextImpl(rootScope)
    }
}

/** A NodeContext handles the lifecycle of a node, and spawns child lifecycles */
class NodeContextImpl
internal constructor(
    override val nodeScope: CoroutineScope
): NodeContext

internal fun NodeContext.createChildScope(): CoroutineScope {
    val nodeJob = nodeScope.coroutineContext[Job]!!
    return nodeScope + SupervisorJob(nodeJob)
}

/** Creates a child NodeContext, which is used to create a child node */
internal fun NodeContext.createChildContext(): NodeContext =
    NodeContextImpl(createChildScope())