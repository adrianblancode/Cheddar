package co.adrianblan.matryoshka

import co.adrianblan.common.DispatcherProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow

/** An Presenter performs business logic inside a Node, and emits a hot observable state */
interface Presenter<T> {
    val state: StateFlow<T>
    val dispatcherProvider: DispatcherProvider
}