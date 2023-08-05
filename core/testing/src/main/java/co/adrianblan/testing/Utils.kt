package co.adrianblan.testing

import kotlinx.coroutines.delay
import kotlin.time.Duration

suspend fun delayAndThrow(delayTime: Duration): Nothing {
    delay(delayTime)
    throw RuntimeException()
}
