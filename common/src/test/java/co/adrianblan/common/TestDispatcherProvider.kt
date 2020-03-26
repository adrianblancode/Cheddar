package co.adrianblan.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.rules.TestWatcher
import org.junit.runner.Description

private val testDispatcher = TestCoroutineDispatcher()

class TestDispatcherProvider: DispatcherProvider {
    override val Main: CoroutineDispatcher = testDispatcher
    override val IO: CoroutineDispatcher = testDispatcher
    override val Default: CoroutineDispatcher = testDispatcher
}

class CoroutineTestRule : TestWatcher() {

    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    override fun starting(description: Description?) {
        super.starting(description)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        testDispatcher.cleanupTestCoroutines()
    }
}