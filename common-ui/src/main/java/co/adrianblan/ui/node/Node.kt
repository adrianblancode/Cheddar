package co.adrianblan.ui.node

import androidx.compose.Composable
import co.adrianblan.common.StateFlow
import co.adrianblan.ui.collectAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/** A node is a composable unit of UI and business logic */
abstract class Node<T>(
    protected val scope: CoroutineScope
) {

    abstract val state: StateFlow<T>

    @Composable
    abstract fun viewDef(state: T)

    @Composable
    fun render() = viewDef(state.collectAsState().value)

    open fun detach() = scope.cancel()

    open fun onBackPressed() = false
}