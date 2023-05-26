package co.adrianblan.matryoshka.presenter

import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.InitialFlow
import kotlinx.coroutines.flow.StateFlow

/** A Presenter performs business logic inside a Node. */
interface Presenter<T> {
    val state: InitialFlow<T>
    val dispatcherProvider: DispatcherProvider
}