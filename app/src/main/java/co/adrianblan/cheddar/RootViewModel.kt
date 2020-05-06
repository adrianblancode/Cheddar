package co.adrianblan.cheddar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import co.adrianblan.cheddar.di.DaggerRootComponent
import co.adrianblan.cheddar.extensions.appComponent
import co.adrianblan.ui.node.AnyNode
import co.adrianblan.ui.node.NodeContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class RootViewModel(
    application: Application,
    // TODO implement saved state
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    private val scope = MainScope()

    val rootNode: AnyNode =
        DaggerRootComponent.factory()
            .build(application.appComponent)
            .rootNodeBuilder()
            .build(NodeContext.createRootContext(scope))

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}