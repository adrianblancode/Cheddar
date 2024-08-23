package co.adrianblan.storyfeed

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PagingTest1 {

//    @Test
//    fun testChunksSimple() = runTest {
//        val flow = MutableStateFlow(0)
//        flow.transformPaginationChunks().test {
//            assertEquals(listOf(0), this.awaitItem())
//            flow.value = 1
//            assertEquals(listOf(1), this.awaitItem())
//        }
//    }

    @Test
    fun testChunksSkipped() = runTest {
        val flow = MutableStateFlow(0)
        flow.transformPaginationChunks().test {
            assertEquals(listOf(0), this.awaitItem())
            flow.value = 3
            assertEquals(listOf(1, 2, 3), this.awaitItem())
        }
    }

    @Test
    fun testChunksResubscribed() = runTest {
        val flow = MutableStateFlow(5)
        flow.transformPaginationChunks().test {
            assertEquals(listOf(0, 1, 2, 3, 4, 5), this.awaitItem())
            flow.value = 7
            assertEquals(listOf(6, 7), this.awaitItem())
        }
    }

    @Test
    fun testMergePagesOne() = runTest {
        val result: List<List<String>> =
            flowOf(1 to listOf("A"))
                .mergePages()
                .toList()

        assertEquals(listOf(listOf("A")), result)
    }

    @Test
    fun testMergePagesOverWrite() = runTest {
        val result: List<List<String>> =
            flowOf(
                1 to listOf("A"),
                1 to listOf("AA")
            )
                .mergePages()
                .toList()

        assertEquals(listOf(listOf("A"), listOf("AA")), result)
    }

    @Test
    fun testMergePagesMultiple() = runTest {
        val result: List<List<String>> =
            flowOf(
                1 to listOf("A"),
                2 to listOf("B"),
                2 to listOf("BB"),
                1 to listOf("AA"),
                2 to listOf("BBBB")
            )
                .mergePages()
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
