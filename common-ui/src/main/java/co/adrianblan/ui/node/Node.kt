package co.adrianblan.ui.node

import androidx.compose.Composable
import androidx.lifecycle.LiveData
import co.adrianblan.ui.observeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/** A node is a composable unit of UI and business logic */
abstract class Node<T>(
    protected val scope: CoroutineScope
) {

    // TODO convert to StateFlow when suspending works with compose
    abstract val state: LiveData<T>

    @Composable
    abstract fun viewDef(state: T)

    @Composable
    fun render() = viewDef(state.observeState())

    open fun detach() = scope.cancel()

    open fun onBackPressed() = false
}