package co.adrianblan.ui

import androidx.compose.*
import co.adrianblan.common.StateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@Composable
fun <T> StateFlow<T>.collectAsState(
    context: CoroutineContext = Dispatchers.Main
): State<T> {

    val flow = this
    val state: MutableState<T> = state { flow.value!! }

    onPreCommit(flow, context) {
        val job = CoroutineScope(context).launch {
            flow.collect { value ->
                state.value = value
            }
        }
        onDispose { job.cancel() }
    }

    return state
}