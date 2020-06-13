package co.adrianblan.matryoshka.root

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import co.adrianblan.matryoshka.node.Node

internal class RootViewModel(
    // TODO use savedstate
    savedStateHandle: SavedStateHandle,
    rootNodeBuilder: () -> Node
) : ViewModel() {
    internal val node: Node = rootNodeBuilder()

    override fun onCleared() {
        super.onCleared()
        node.cancel()
    }
}

internal class RootViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val rootNodeBuilder: () -> Node
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        savedStateHandle: SavedStateHandle
    ): T {
        require(modelClass.isAssignableFrom(RootViewModel::class.java))

        @Suppress("UNCHECKED_CAST")
        return RootViewModel(
            savedStateHandle,
            rootNodeBuilder
        ) as T
    }
}