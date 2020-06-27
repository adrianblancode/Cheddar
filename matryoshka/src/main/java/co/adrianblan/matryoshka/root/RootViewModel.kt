package co.adrianblan.matryoshka.root

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistry
import co.adrianblan.matryoshka.node.AnyNode
import co.adrianblan.matryoshka.node.Node
import co.adrianblan.matryoshka.node.NodeFactory
import co.adrianblan.matryoshka.node.NodeStoreImpl

internal class RootViewModel(
    private val savedStateRegistry: SavedStateRegistry,
    rootNodeFactory: NodeFactory<AnyNode>
) : ViewModel() {

    private val savedState: Bundle? = savedStateRegistry.consumeRestoredStateForKey(ROOT_VIEWMODEL)

    private val nodeStore =
        NodeStoreImpl(savedState = savedState?.getParcelable(NODE_STORE_SAVED_STATE))

    private val nodeContainer = createRootNodeContainer(
        scope = viewModelScope,
        rootNodeFactory = rootNodeFactory,
        nodeStore = nodeStore,
        nodeSavedState = savedState?.getParcelable(NODE_SAVED_STATE)
    )

    internal val node: Node = nodeContainer.node

    private val savedStateProvider =
        SavedStateRegistry.SavedStateProvider {
            Bundle().apply {
                // TODO this does not work until Kotlin 1.4
                // putParcelable(NODE_STORE_SAVED_STATE, nodeStore.saveState())
                // putParcelable(NODE_SAVED_STATE, node.saveState())
            }
        }
            .apply {
                savedStateRegistry.registerSavedStateProvider(ROOT_VIEWMODEL, this)
            }


    override fun onCleared() {
        super.onCleared()
        nodeStore.detach()
        node.detach()
    }

    companion object {
        private const val ROOT_VIEWMODEL = "RootViewModel"
        private const val NODE_SAVED_STATE = "RootViewModel.SavedState"
        private const val NODE_STORE_SAVED_STATE = "RootViewModel.NodeStoreSavedState"
    }
}

internal class RootViewModelFactory(
    private val savedStateRegistry: SavedStateRegistry,
    private val rootNodeFactory: NodeFactory<Node>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(RootViewModel::class.java))

        @Suppress("UNCHECKED_CAST")
        return RootViewModel(
            savedStateRegistry = savedStateRegistry,
            rootNodeFactory = rootNodeFactory
        ) as T
    }
}