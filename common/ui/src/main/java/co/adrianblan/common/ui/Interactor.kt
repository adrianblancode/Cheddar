package co.adrianblan.common.ui

import androidx.annotation.CallSuper
import co.adrianblan.common.DispatcherProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class Interactor {

    abstract val dispatcherProvider: DispatcherProvider

    private val attachJob: CoroutineContext = SupervisorJob()
    protected val attachScope =
        CoroutineScope(Dispatchers.Main.immediate + attachJob)

    @CallSuper
    open fun onDetach() {
        attachJob.cancel()
    }
}