package co.adrianblan.matryoshka

import androidx.annotation.CallSuper
import androidx.compose.Composable
import kotlinx.coroutines.*

typealias AnyNode = Node

/** A Node is a composable unit of UI and business logic */
abstract class Node(
    internal val nodeContext: NodeContext
) {
    private val nodeManager = NodeManager()
    internal val isActive = nodeContext.nodeScope.isActive

    /** A child scope of the node scope, with the same lifetime */
    protected val workScope = nodeContext.createChildScope()

    /**
     * Creates and attaches a child node to the current node.
     * The child will be considered active and working, until it is detached or the root node is cancelled.
     */
    protected fun <T : Node> attachChild(
        factory: (childContext: NodeContext) -> T
    ): T {
        val childContext: NodeContext = nodeContext.createChildContext()
        val childNode = factory(childContext)

        // TODO add in when builders are mocked
        /*
        require(childNode.nodeContext === childContext) {
            "Child nodes must be created with the provided childContext"
        }
         */

        // TODO key

        nodeManager.registerChild(childNode)
        return childNode
    }

    /** Renders the result of this node. */
    @Composable
    abstract fun render()

    internal fun detach() {
        nodeContext.nodeScope.cancel()
    }

    /**
     * Called when the node and have been detached from the tree.
     * Should always be called after the node scope has been cancelled
     */
    @CallSuper
    internal open fun onDetached() {
        require(!nodeContext.nodeScope.isActive) {
            "Node scope must be cancelled for a node to be detached"
        }
    }

    /**
     * Returns whether the back press has been consumed internally,
     * or if it should be handled by the parent.
     *
     * A typical use case for overriding would be to delegate this to a node backstack,
     * and return true as long as we have popped the back stack.
     **/
    open fun onBackPressed() = false
}


// TODO saved state

/** NodeManager attaches and detaches the children of a node. */
internal class NodeManager {
    private val children = mutableListOf<Node>()

    internal fun registerChild(node: Node) =
        children.add(node)

    internal fun detachChild(node: Node) {
        children.remove(node)

        require(node.isActive) {
            "A child node must not cancel their own scope, and a child node must only be detached once"
        }

        node.detach()
        node.dispatchDetach()
    }

    /**
     * Recursively detaches all child nodes from the tree,
     * and calls onDetached when all children have been detached
     */
    private fun Node.dispatchDetach() {
        children.forEach {
            it.dispatchDetach()
        }
        onDetached()
    }
}