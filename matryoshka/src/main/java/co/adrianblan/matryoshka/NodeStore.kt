package co.adrianblan.matryoshka

import kotlinx.coroutines.*

/** NodeStore manages attaching and detaching children to a parent node */
interface NodeStore {

    /** Gets any child with the specified key, otherwise creates one */
    fun <T : AnyNode> getChild(key: String, childBuilder: () -> T): T

    /** Detaches a child, and cancels both it's scope and any subchildren recursively */
    fun detachChild(key: String): Boolean
}

internal fun nodeStore(nodeScope: CoroutineScope): NodeStore =
    NodeStoreImpl(nodeScope)

private class NodeStoreImpl(
    nodeScope: CoroutineScope
) : NodeStore {

    private val children = mutableMapOf<String, AnyNode>()

    init {
        nodeScope.coroutineContext[Job]!!
            .invokeOnCompletion {
                children.forEach { it.value.onCleared()  }
            }
    }

    override fun <T : AnyNode> getChild(key: String, childBuilder: () -> T): T {

        children[key]?.let {
            return@getChild it as T
        }

        val child: T = childBuilder()

        children[key] = child

        return child
    }

    override fun detachChild(key: String): Boolean {
        val child: AnyNode? = children[key]

        child?.let {
            it.onCleared()
        }

        return children.remove(key) != null
    }
}