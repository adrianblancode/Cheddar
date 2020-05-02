package co.adrianblan.cheddar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import co.adrianblan.cheddar.di.DaggerRootComponent
import co.adrianblan.cheddar.extensions.appComponent
import co.adrianblan.common.asParentScope
import co.adrianblan.ui.node.Node
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class RootViewModel(
    application: Application,
    // TODO implement saved state
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    private val scope = MainScope()

    val rootNode: Node =
        DaggerRootComponent.factory()
            .build(application.appComponent)
            .rootNodeBuilder()
            .build(scope.asParentScope())

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}