package co.adrianblan.common.ui

import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.ParentScope
import kotlinx.coroutines.*

abstract class Interactor {

    abstract val dispatcherProvider: DispatcherProvider

    abstract val parentScope: ParentScope

    val scope: CoroutineScope by lazy {
        parentScope.createChildScope(dispatcherProvider)
    }
}