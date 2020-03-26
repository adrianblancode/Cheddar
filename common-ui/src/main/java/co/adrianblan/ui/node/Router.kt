package co.adrianblan.ui.node

/** A Router is used by a parent Node to attach and detach child Nodes */
interface Router {
    val nodes: List<Node<*>>
    fun onBackPressed(): Boolean
}

class StackRouter constructor(
    initialState: List<Node<*>>,
    private val onRouterUpdate: Router.() -> Unit
) : Router {

    override var nodes: MutableList<Node<*>> = initialState.toMutableList()
        private set

    init {
        onRouterUpdate()
    }

    fun push(node: Node<*>) {
        nodes.add(node)
        onRouterUpdate(this)
    }

    private fun pop() {
        nodes.removeAt(nodes.size - 1)
            .apply {
                detach()
            }

        onRouterUpdate(this)
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