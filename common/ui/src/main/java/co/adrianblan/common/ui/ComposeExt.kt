package co.adrianblan.common.ui

import androidx.compose.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import timber.log.Timber

inline fun <reified T> observe(data: LiveData<T>) = effectOf<T> {
    // Ugly hack with default value, otherwise will reuse state for other LiveData even if other types
    // Throw if LiveData value is null
    val result = +stateFor(data.value) { data.value!! }
    val observer = +memo { Observer<T> { result.value = it } }

    +onCommit(data) {
        data.observeForever(observer)
        onDispose { data.removeObserver(observer) }
    }

    result.value
}