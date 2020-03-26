package co.adrianblan.ui.node

/** A Router is used by a parent Node to attach and detach child Nodes */
interface Router {
    val nodes: List<Node<*>>
    fun onBackPressed(): Boolean
}

class StackRouter constructor(
    initialState: List<Node<*>>,
    private val onNodesUpdate: (List<Node<*>>) -> Unit
) : Router {

    override lateinit var nodes: MutableList<Node<*>>
        private set

    init {
        nodes = initialState.toMutableList()
        onNodesUpdate(nodes)
    }

    fun push(node: Node<*>) {
        nodes.add(node)
        onNodesUpdate(nodes)
    }

    private fun pop() {
        nodes.removeAt(nodes.size - 1)
            .apply {
                detach()
            }

        onNodesUpdate(nodes)
    }

    private fun canPop() = nodes.size > 1

    override fun onBackPressed(): Boolean {
        // Iterate through stack of children, see if any children handle it
        nodes.reversed()
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