package co.adrianblan.matryoshka.root

import android.os.Parcelable
import androidx.activity.ComponentActivity
import co.adrianblan.matryoshka.node.AnyNode
import co.adrianblan.matryoshka.node.NodeFactory
import co.adrianblan.matryoshka.node.NodeStore
import co.adrianblan.matryoshka.node.NodeStoreImpl
import co.adrianblan.matryoshka.test.TestNodeStore
import co.adrianblan.matryoshka.utils.invokeOnCompletion
import kotlinx.coroutines.CoroutineScope

/** Creates a root node, backed by the scope of a ViewModel. */
fun ComponentActivity.createVmRootNode(
    rootNodeFactory: NodeFactory<AnyNode>
): Lazy<AnyNode> = lazy {
    RootViewModelFactory(savedStateRegistry, rootNodeFactory)
        .create(RootViewModel::class.java)
        .node
}

fun <T : AnyNode> createRootNodeContainer(
    scope: CoroutineScope,
    rootNodeFactory: NodeFactory<T>,
    nodeSavedState: Parcelable?,
    nodeStore: NodeStore
): RootNodeContainer<T> = RootNodeContainer(
    scope = scope,
    rootNodeFactory = rootNodeFactory,
    nodeSavedState = nodeSavedState,
    nodeStore = nodeStore
)

/** Creates a test node, which will be cancelled when the test scope is cancelled. */
fun <T : AnyNode> createTestNodeContainer(
    testScope: CoroutineScope,
    rootNodeFactory: NodeFactory<T>,
    nodeSavedState: Parcelable? = null
): RootNodeContainer<T> = RootNodeContainer(
    scope = testScope,
    rootNodeFactory = rootNodeFactory,
    nodeSavedState = nodeSavedState,
    nodeStore = TestNodeStore
)

fun <T : AnyNode> createTestNode(
    testScope: CoroutineScope,
    rootNodeFactory: NodeFactory<T>
): T = createTestNodeContainer(testScope, rootNodeFactory).node

class RootNodeContainer<T : AnyNode>(
    scope: CoroutineScope,
    rootNodeFactory: NodeFactory<T>,
    nodeSavedState: Parcelable? = null,
    private val nodeStore: NodeStore
) {

    val node: T = rootNodeFactory.create(
        savedState = nodeSavedState,
        nodeStore = nodeStore
    )

    fun saveState(): Parcelable? = node.saveState()

    init {
        scope.invokeOnCompletion {
            nodeStore.detach()
            node.detach()
        }
    }
}