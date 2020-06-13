package co.adrianblan.matryoshka.node

import co.adrianblan.matryoshka.utils.invokeOnCompletion
import kotlinx.coroutines.*

/** NodeStore manages attaching and detaching children to a parent node. */
interface NodeStore {

    /** Returns an existing matching child for the key, otherwise creates one. */
    fun <T : AnyNode> child(key: String, childBuilder: () -> T): T

}

fun nodeStore(nodeScope: CoroutineScope): NodeStore =
    NodeStoreImpl(nodeScope)

private class NodeStoreImpl(
    private val nodeScope: CoroutineScope
) : NodeStore {

    private val children = mutableMapOf<String?, AnyNode>()

    init {
        // When node scope is cancelled, cancel all children
        nodeScope.invokeOnCompletion {
            children.values.forEach { it.cancel() }
        }
    }

    /**
     * Returns an existing matching child for the key, otherwise creates one.
     * Throws if key is already in use by a child of a different type.
     */
    override fun <T : AnyNode> child(key: String, childBuilder: () -> T): T {

        // If there exists with the same type and key, return it
        children[key]
            ?.let {

                // Require correct type
                require(it::class as? T != null) {
                    "Key $key is already registered to a child: ${it::class.simpleName}"
                }

                return@child it as T
            }

        val child: T = childBuilder()

        children[key] = child

        // When child is detached, remove it from hierarchy
        nodeScope.launch {
            child.awaitDetach()
            children.remove(key)
        }

        return child
    }
}

/** Convenience function for calling child with class name as key. */
inline fun <reified T : AnyNode> NodeStore.child(noinline childBuilder: () -> T): T =
    child(T::class.simpleName!!, childBuilder)