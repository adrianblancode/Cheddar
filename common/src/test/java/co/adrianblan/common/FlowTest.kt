package co.adrianblan.common

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowTest {

    @Test
    fun testScanReducePagesOne() {
        runBlockingTest {
            val result: List<List<String>> =
                flowOf(1 to listOf("A"))
                    .scanReducePages()
                    .toList()

            assertEquals(listOf(listOf("A")), result)
        }
    }

    @Test
    fun testScanReducePagesOverWrite() {
        runBlockingTest {
            val result: List<List<String>> =
                flowOf(
                    1 to listOf("A"),
                    1 to listOf("AA")
                )
                    .scanReducePages()
                    .toList()

            assertEquals(listOf(listOf("A"), listOf("AA")), result)
        }
    }

    @Test
    fun testScanReducePagesMultiple() {
        runBlockingTest {
            val result: List<List<String>> =
                flowOf(
                    1 to listOf("A"),
                    2 to listOf("B"),
                    2 to listOf("BB"),
                    1 to listOf("AA"),
                    2 to listOf("BBBB")
                )
                    .scanReducePages()
                    .toList()

            val expected: List<List<String>> = listOf(
                listOf("A"),
                listOf("A, B"),
                listOf("A", "BB"),
                listOf("AA", "BB"),
                listOf("AA", "BBBB")
            )

            assertEquals(expected.size, result.size)

            // Different containers, same contents
            assertEquals(expected.toString(), result.toString())
        }
    }
}