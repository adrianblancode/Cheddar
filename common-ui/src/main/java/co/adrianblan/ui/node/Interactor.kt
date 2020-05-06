package co.adrianblan.ui.node

import co.adrianblan.common.DispatcherProvider
import kotlinx.coroutines.*

// TODO rename

/** An Interactor performs business logic inside a Node */
abstract class Interactor {
    abstract val dispatcherProvider: DispatcherProvider
}