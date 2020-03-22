package co.adrianblan.ui

import androidx.compose.Model
import androidx.compose.frames.ModelList
import androidx.compose.frames.modelListOf

interface Router {
    val nodes: List<Node>
    fun onBackPressed(): Boolean
}

@Model
class StackRouter private constructor(
    override val nodes: ModelList<Node>
): Router {

    fun push(node: Node) =
        nodes.add(node)

    private fun pop() {
        nodes.removeAt(nodes.size - 1)
            .apply {
                detach()
            }
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

    companion object {
        fun of(initialNode: Node): StackRouter =
            StackRouter(modelListOf(initialNode))
    }
}