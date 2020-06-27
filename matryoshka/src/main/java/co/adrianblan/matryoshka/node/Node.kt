package co.adrianblan.matryoshka.node

import android.os.Parcelable
import androidx.compose.Composable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first

typealias AnyNode = Node

/** A Node is a composable unit of UI and business logic. */
abstract class Node(
    /** A scope with the lifetime of the node. */
    protected val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
) {

    /** Emits when node detaches, as well as if node has previously been detached. */
    private val detachSignalChannel = ConflatedBroadcastChannel<Unit>()

    /** Renders the result of this node. */
    @Composable
    abstract fun render()

    /** Returns the saved state of the node, called before detach. */
    open fun saveState(): Parcelable? = null

    /** Detaches node, cancels it's scope and notifies it's been detached. */
    internal fun detach() {
        require(scope.isActive && detachSignalChannel.valueOrNull == null) {
            "Cannot detach an already detached node"
        }

        scope.cancel()
        detachSignalChannel.offer(Unit)
    }

    /** Suspends until detach, or returns immediately if node has already been detached. */
    internal suspend fun awaitDetach() =
        coroutineScope {
            detachSignalChannel.asFlow().first()
        }

    /**
     * Returns true when the back press has been consumed internally,
     * or false if it should be handled by the parent.
     *
     * A typical use case for overriding would be to delegate this to a node backstack,
     * and return true as long as we have popped the back stack.
     **/
    open fun onBackPressed() = false
}