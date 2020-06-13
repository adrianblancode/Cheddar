package co.adrianblan.matryoshka.presenter

import co.adrianblan.common.DispatcherProvider
import kotlinx.coroutines.flow.StateFlow

/** A Presenter performs business logic inside a Node. */
interface Presenter<T> {
    val state: StateFlow<T>
    val dispatcherProvider: DispatcherProvider
}