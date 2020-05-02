package co.adrianblan.ui.node

import androidx.compose.Composable
import co.adrianblan.common.StateFlow
import co.adrianblan.ui.collectAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/** A node is a composable unit of UI and business logic */
abstract class Node(
    protected val scope: CoroutineScope
) {

    @Composable
    abstract fun render()

    open fun detach() = scope.cancel()

    open fun onBackPressed() = false
}