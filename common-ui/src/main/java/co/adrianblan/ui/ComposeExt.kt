package co.adrianblan.ui

import androidx.compose.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@Composable
inline fun <reified T> observe(data: LiveData<T>): T {
    // Throw if LiveData value is null
    val result = stateFor(data) { data.value!! }
    val observer = remember { Observer<T> { result.value = it } }

    onCommit(data) {
        data.observeForever(observer)
        onDispose { data.removeObserver(observer) }
    }

    return result.value
}