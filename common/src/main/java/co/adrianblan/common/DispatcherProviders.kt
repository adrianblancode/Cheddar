package co.adrianblan.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


@Suppress("PropertyName")
interface DispatcherProvider {
    val Main: CoroutineDispatcher
    val IO: CoroutineDispatcher
    val Default: CoroutineDispatcher
}


object DefaultDispatcherProvider: DispatcherProvider {
    override val Main: CoroutineDispatcher = Dispatchers.Main
    override val IO: CoroutineDispatcher = Dispatchers.IO
    override val Default: CoroutineDispatcher = Dispatchers.Default
}