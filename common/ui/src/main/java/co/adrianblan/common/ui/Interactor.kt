package co.adrianblan.common.ui

import androidx.annotation.CallSuper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class Interactor {

    private val attachJob: CoroutineContext = SupervisorJob()
    protected val attachScope =
        CoroutineScope(Dispatchers.Main.immediate + attachJob)

    @CallSuper
    open fun onDetach() {
        attachJob.cancel()
    }
}