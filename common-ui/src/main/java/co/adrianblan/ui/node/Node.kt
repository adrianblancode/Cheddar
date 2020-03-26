package co.adrianblan.ui.node

import androidx.compose.Composable
import androidx.lifecycle.LiveData
import co.adrianblan.ui.observe
import timber.log.Timber

/** A node is a composable unit of UI and business logic */
abstract class Node<T> {

    // TODO convert to StateFlow when suspending works with compose
    abstract val viewState: LiveData<T>

    protected abstract val viewDef: @Composable() (T) -> Unit

    @Composable
    fun nodeView() = NodeView(viewState, viewDef)

    abstract fun detach()

    open fun onBackPressed() = false
}

/** Observes view state and composes the node view */
@Composable
private fun <T> NodeView(viewStateLiveData: LiveData<T>, viewDef: @Composable() (T) -> Unit) {
    val viewState: T? = observe(viewStateLiveData)
    NodeViewInner(viewState, viewDef)
}

@Composable
private fun <T> NodeViewInner(viewState: T?, composeView: @Composable() (T) -> Unit) {
    viewState?.let {
        composeView(it)
    } ?: Timber.d("Cannot compose view, viewstate was null")
}