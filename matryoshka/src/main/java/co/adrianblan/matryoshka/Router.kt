package co.adrianblan.matryoshka

import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** A Router is used by a parent Node to attach and detach child Nodes */
interface Router {
    val state: StateFlow<List<AnyNode>>
    fun onBackPressed(): Boolean
}

class StackRouter constructor(
    initialState: List<AnyNode>
) : Router {

    override val state: MutableStateFlow<List<AnyNode>> =
        MutableStateFlow(initialState)

    fun push(node: AnyNode) {
        state.value = state.value + node
    }

    fun pop() {

        val nodes: MutableList<AnyNode> = state.value.toMutableList()

        nodes.removeAt(nodes.size - 1)
            .also { it.detach() }

        state.value = nodes
    }

    private fun canPop() = state.value.size > 1

    override fun onBackPressed(): Boolean {
        // Iterate through stack of children, see if any children handle it
        state.value.reversed()
            .forEach { node ->
                if (node.onBackPressed()) {
                    return true
                }
            }

        return if (canPop()) {
            pop()
            true
        } else false
    }
}