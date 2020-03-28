package co.adrianblan.ui

import androidx.compose.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import co.adrianblan.common.StateFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@Composable
fun <T> LiveData<T>.observeState(): T =
    observe(this)

@Composable
fun <T> observe(liveData: LiveData<T>): T {

    // throw if null value
    val result = stateFor(liveData) { liveData.value!! }
    val observer = remember { Observer<T> { result.value = it } }

    onCommit(liveData) {
        liveData.observeForever(observer)
        onDispose { liveData.removeObserver(observer) }
    }

    return result.value
}


/* TODO re-enable when kapt works with compose
@Composable
fun <T> StateFlow<T>.observe(): T {

    val stateFlow = this

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
 */