package co.adrianblan.matryoshka.node

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*

/** NodeStore manages attaching and detaching children to a parent node. */
interface NodeStore {

    /** Returns an existing matching child for the key, otherwise creates one. */
    fun <T : AnyNode> child(key: String, childFactory: NodeFactory<T>): T

    fun detach()

}

internal class NodeStoreImpl(
    savedState: NodeStoreSavedState?
) : NodeStore {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val children = mutableMapOf<String, AnyNode>()
    private val childNodeStores = mutableMapOf<String, NodeStoreImpl>()

    private val initialChildSavedStates: MutableMap<String, Parcelable?> =
        savedState?.childSavedStates.orEmpty().toMutableMap()

    private val initialNodeStoreSavedStates: MutableMap<String, NodeStoreSavedState?> =
        savedState?.childNodeStoresSavedStates.orEmpty().toMutableMap()

    /**
     * Returns an existing matching child for the key, otherwise creates one.
     * Throws if key is already in use by a child of a different type.
     */
    override fun <T : AnyNode> child(key: String, childFactory: NodeFactory<T>): T {

        // If there exists a node with the same type and key, return it
        children[key]
            ?.let {

                // Require correct type
                require(it::class as? T != null) {
                    "Key $key is already registered to a child: ${it::class.simpleName}"
                }

                return@child it as T
            }

        val childNodeStore = NodeStoreImpl(
            savedState = initialNodeStoreSavedStates[key]
        )

        val child: T = childFactory.create(
            savedState = initialChildSavedStates[key],
            nodeStore = childNodeStore
        )

        childNodeStore.flushSavedStates()

        children[key] = child
        childNodeStores[key] = childNodeStore

        // When child is detached, remove it from hierarchy
        scope.launch {
            child.awaitDetach()

            // Cancel and remove children of the node first
            childNodeStores.remove(key).also { detach() }

            children.remove(key)
        }

        return child
    }

    /** Saves the state of all children and their node stores */
    internal fun saveState(): NodeStoreSavedState =
        NodeStoreSavedState(
            childSavedStates = children.mapValues { it.value.saveState() },
            childNodeStoresSavedStates = childNodeStores.mapValues { it.value.saveState() }
        )

    /** Called when recreation is done, and previous saved state should be flushed */
    private fun flushSavedStates() {
        initialChildSavedStates.clear()
        initialNodeStoreSavedStates.clear()
    }

    /** Before node is detached, node store will be detached and recursively detach children */
    override fun detach() {
        scope.cancel()

        childNodeStores.values.forEach { it.detach() }
        children.values.forEach { it.detach() }
    }
}

@Parcelize
data class NodeStoreSavedState(
    val childSavedStates: Map<String, Parcelable?>,
    val childNodeStoresSavedStates: Map<String, NodeStoreSavedState>
): Parcelable