package co.adrianblan.matryoshka

import androidx.annotation.CallSuper
import androidx.compose.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.util.*

typealias AnyNode = Node

/** A Node is a composable unit of UI and business logic */
abstract class Node {

    /** A scope with the lifetime of the node */
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /** Node store which manages the children of the node */
    protected val nodeStore: NodeStore =
        nodeStore(scope)

    @CallSuper
    open fun onCleared() {
        scope.cancel()
    }

    /** Renders the result of this node. */
    @Composable
    abstract fun render()

    /**
     * Returns whether the back press has been consumed internally,
     * or if it should be handled by the parent.
     *
     * A typical use case for overriding would be to delegate this to a node backstack,
     * and return true as long as we have popped the back stack.
     **/
    open fun onBackPressed() = false
}