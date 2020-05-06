package co.adrianblan.ui.node

import co.adrianblan.common.DispatcherProvider
import kotlinx.coroutines.*

/** An Interactor performs business logic inside a Node */
abstract class Interactor(
    protected val scope: CoroutineScope
) {
    abstract val dispatcherProvider: DispatcherProvider
}