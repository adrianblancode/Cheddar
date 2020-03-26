package co.adrianblan.ui.node

import androidx.compose.Composable
import co.adrianblan.common.StateFlow
import co.adrianblan.ui.observe
import timber.log.Timber

/** A node is a composable unit of UI and business logic */
abstract class Node<T> {

    abstract val viewStateFlow: StateFlow<T>

    protected abstract val composeView: @Composable() (T) -> Unit

    @Composable
    fun nodeView() {
        val viewState: T? = observe(viewStateFlow)
        viewState?.let { composeView(it) }
    }

    abstract fun detach()

    open fun onBackPressed() = false
}

/** Observes view state and composes the node view */
@Composable
private fun <T> NodeView(viewStateFlow: StateFlow<T>, composeView: @Composable() (T) -> Unit) {
    val viewState: T? = observe(viewStateFlow)

    viewState?.let {
        composeView(it)
    } ?: Timber.d("Cannot compose view, viewstate was null")
}