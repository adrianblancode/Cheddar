package co.adrianblan.ui

import androidx.compose.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import co.adrianblan.common.StateFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@Composable
fun <T> observe(stateFlow: StateFlow<T>): T? {
    val result = stateFor(stateFlow) { stateFlow.value }

    onCommit(stateFlow) {
        val job = GlobalScope.launch {
            stateFlow
                .collect { value ->
                    result.value = value
                }
        }
        onDispose { job.cancel() }
    }

    return result.value
}