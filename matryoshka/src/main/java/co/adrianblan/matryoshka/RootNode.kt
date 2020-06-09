package co.adrianblan.matryoshka

import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope

/**
 * Creates a root node, backed by the scope of a ViewModel.
 * Use with
 */
fun ComponentActivity.createRootNode(
    rootNodeBuilder: () -> AnyNode
): Lazy<AnyNode> = lazy {
    RootViewModelFactory(this, rootNodeBuilder)
        .create(RootViewModel::class.java)
        .node
}

/** Creates a test node, which will be cancelled when the test scope is cancelled */
fun <T : AnyNode> createTestNode(
    testScope: CoroutineScope,
    nodeBuilder: () -> T
): T =
    TestNodeContainer(testScope, nodeBuilder).node

private class TestNodeContainer<T : AnyNode>(
    testScope: CoroutineScope,
    nodeBuilder: () -> T
) {
    val node: T = nodeBuilder()

    init {
        testScope.invokeOnCompletion { node.onCleared() }
    }
}