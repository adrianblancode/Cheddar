package co.adrianblan.matryoshka.router

import co.adrianblan.common.mapStateFlow
import co.adrianblan.matryoshka.node.AnyNode
import co.adrianblan.matryoshka.node.NodeStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Router for a stack of nodes.
 */
abstract class StackRouter<T : Any> constructor(
    override val nodeStore: NodeStore,
    initialState: List<T>
) : Router<T>() {

    constructor(nodeStore: NodeStore, initialState: T)
            : this(nodeStore, listOf(initialState))

    init {
        require (initialState.size == initialState.distinct().size) {
            "All keys in initial state must be distinct"
        }
    }

    data class StackNode<T>(
        val key: T,
        val node: AnyNode
    )

    /** List of all nodes in the stack */
    private val _state: MutableStateFlow<List<StackNode<T>>> =
        MutableStateFlow(
            initialState.map { key ->
                StackNode(
                    key,
                    nodeStore.child(key.toString()) { key.createNode() }
                )
            }
        )

    val state: StateFlow<List<StackNode<T>>> = _state

    val activeNode: StateFlow<AnyNode> =
        state.mapStateFlow { it.last().node }

    /**
     * Pushes a node to the top of the stack.
     * Throws if duplicate key.
     */
    fun push(key: T) {

        require (key !in state.value.map { it.key }) {
            "Cannot push a node with existing key: $key"
        }

        _state.value = state.value + StackNode(
            key,
            nodeStore.child(key.toString()) { key.createNode() }
        )
    }

    /**
     * Pops a node from the top of the stack.
     * Throws if stack is empty.
     */
    fun pop() {

        require(state.value.isNotEmpty()) { "Cannot pop an empty stack" }

        val newState: MutableList<StackNode<T>> = state.value.toMutableList()

        newState.removeAt(newState.size - 1)
            .also { it.node.detach() }

        _state.value = newState.toList()
    }


    private fun canPop() = state.value.size > 1

    /** Iterate through stack of children, and delegates the back press if any can handle it */
    fun onBackPressed(): Boolean {
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