package co.adrianblan.common.ui

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class Interactor {

    private val attachJob: CoroutineContext = SupervisorJob()
    protected val attachScope =
        CoroutineScope(Dispatchers.Main.immediate + attachJob)

    fun onDetach() {
        attachJob.cancel()
    }
}