package co.adrianblan.ui.node

import androidx.compose.Composable
import kotlinx.coroutines.*

typealias AnyNode = Node

/** A node is a composable unit of UI and business logic */
abstract class Node(
    internal val nodeContext: NodeContext
) {
    private val nodeManager = NodeManager()

    val scope = nodeContext.createChildScope()

    fun <T: Node> createChild(creator: (childnodeContext: NodeContext) -> T): T {
        val childNode = creator(nodeContext.createChildContext())

        require(childNode.nodeContext != nodeContext) {
            "Cannot create a child node with parent context"
        }

        // TODO key

        nodeManager.registerChild(childNode)
        return childNode
    }

    @Composable
    abstract fun render()

    fun detach() = nodeContext.scope.cancel()

    open fun onBackPressed() = false
}

class NodeContext
private constructor(internal val scope: CoroutineScope) {

    fun createChildScope(): CoroutineScope {
        val parentJob = scope.coroutineContext[Job]!!
        return scope + SupervisorJob(parentJob)
    }

    internal fun createChildContext(): NodeContext =
        NodeContext(createChildScope())

    companion object {
        fun createRootContext(rootScope: CoroutineScope) =
            NodeContext(rootScope)
    }
}

internal class NodeManager {
    private val children = mutableListOf<NodeContext>()

    internal fun registerChild(node: AnyNode) =
        children.add(node.nodeContext)
}