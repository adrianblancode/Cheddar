package co.adrianblan.matryoshka

import co.adrianblan.common.mapStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** A Router is used by a parent Node to attach and detach child Nodes */
interface Router<T> {
    val activeNode: StateFlow<AnyNode>

    fun T.key(): String
    fun T.createNode(): AnyNode

    fun onBackPressed(): Boolean
}

abstract class StackRouter<T> constructor(
    private val nodeStore: NodeStore,
    initialState: List<T>
) : Router<T> {

    constructor(
        nodeStore: NodeStore,
        initialState: T
    ) : this(nodeStore, listOf(initialState))

    data class StackNode<T>(
        val key: T,
        val node: AnyNode
    )

    val state: MutableStateFlow<List<StackNode<T>>> =
        MutableStateFlow(
            initialState.map { key ->
                StackNode(key, key.createNode())
            }
        )

    override val activeNode: StateFlow<AnyNode> =
        state.mapStateFlow { it.last().node }

    override fun T.key(): String = this.toString()

    fun push(key: T) {
        state.value = state.value + StackNode(key, key.createNode())
    }

    fun pop() {

        val newState: MutableList<StackNode<T>> = state.value.toMutableList()

        newState.removeAt(newState.size - 1)
            .also { nodeStore.detachChild(it.key.key()) }

        state.value = newState.toList()
    }

    private fun canPop() = state.value.size > 1

    override fun onBackPressed(): Boolean {
        // Iterate through stack of children, see if any children handle it
        state.value.reversed()
            .forEach { pair ->
                if (pair.node.onBackPressed()) {
                    return true
                }
            }

        return if (canPop()) {
            pop()
            true
        } else false
    }
}