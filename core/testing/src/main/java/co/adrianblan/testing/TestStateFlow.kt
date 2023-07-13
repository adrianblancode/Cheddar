package co.adrianblan.testing

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope

class TestStateFlow<T>
internal constructor(
    private val stateFlow: StateFlow<T>,
    private val scope: TestCoroutineScope
) {
    private val _values = mutableListOf<T>()

    val values: List<T> = _values
    val value get() = stateFlow.value

    init {
        scope.launch {
            stateFlow.collect {
                _values.add(it)
            }
        }
    }
}

fun <T> StateFlow<T>.test(scope: TestCoroutineScope) =
    TestStateFlow(this, scope)