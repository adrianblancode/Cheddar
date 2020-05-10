package co.adrianblan.cheddar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import co.adrianblan.cheddar.di.DaggerRootComponent
import co.adrianblan.cheddar.utils.appComponent
import co.adrianblan.matryoshka.AnyNode
import co.adrianblan.matryoshka.NodeContext
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
            .build(NodeContext.createRoot(scope))

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}