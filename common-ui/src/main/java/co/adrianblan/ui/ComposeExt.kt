package co.adrianblan.ui

import androidx.compose.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@Composable
inline fun <reified T> observe(data: LiveData<T>): T {
    // Ugly hack with default value, otherwise will reuse state for other LiveData even if other types
    // Throw if LiveData value is null
    val result = stateFor(data.value) { data.value!! }
    val observer = remember { Observer<T> { result.value = it } }

    onCommit(data) {
        data.observeForever(observer)
        onDispose { data.removeObserver(observer) }
    }

    return result.value
}