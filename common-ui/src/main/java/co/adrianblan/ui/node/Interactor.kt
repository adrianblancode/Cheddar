package co.adrianblan.ui.node

import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.ParentScope
import kotlinx.coroutines.*

/** An Interactor performs business logic inside a Node */
abstract class Interactor(
    private val scope: CoroutineScope
) {
    abstract val dispatcherProvider: DispatcherProvider
}