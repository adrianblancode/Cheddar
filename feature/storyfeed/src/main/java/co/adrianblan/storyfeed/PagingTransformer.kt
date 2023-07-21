package co.adrianblan.storyfeed

import co.adrianblan.common.onFirst
import co.adrianblan.domain.DecoratedStory
import co.adrianblan.model.StoryId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlin.math.min

typealias PageIndex = Int
typealias KeyedPage = Pair<Int, List<DecoratedStory>>

/**
 * Takes a source of page index to story ids,
 * and a source story id to flow of story,
 * returns a flow of content for all pages.
 */
internal fun StateFlow<PageIndex>.observePages(
    pageStoryIdsSource: (PageIndex) -> List<StoryId>,
    storyFlowSource: (StoryId) -> Flow<DecoratedStory>
): Flow<List<DecoratedStory>> {
    return observePages { pageIndex ->
        val pageStories: List<Flow<DecoratedStory>> =
            pageStoryIdsSource(pageIndex)
                .map { storyId -> storyFlowSource(storyId) }

        combine(pageStories) { it.toList() }
    }
}

/**
 * Takes a source of page index to flow of content for the page,
 * and returns a flow of content for all pages.
 */
private fun StateFlow<PageIndex>.observePages(
    pageFlowSource: (PageIndex) -> Flow<List<DecoratedStory>>
): Flow<List<DecoratedStory>> {

    val pageIndexFlow = this

    // Flow of indexes to a chunk of pages to load
    // Since we don't have to start at zero, we emit chunks of pages
    val chunkedPageIndexesFlow: Flow<List<PageIndex>> = flow {
        var lastPageIndex = 0

        // StateFlow can skip values, so emit intermediate pages
        pageIndexFlow.collect { pageIndex ->
            emit((lastPageIndex .. pageIndex).toList())
            lastPageIndex = pageIndex
        }
    }

    return chunkedPageIndexesFlow.flatMapConcat { pageIndexes: List<PageIndex> ->

        // List of flows for each page
        val keyedPageFlows: List<Flow<KeyedPage>> =
            pageIndexes.map { pageIndex ->
                pageFlowSource(pageIndex)
                    .map { pageStories ->
                        pageIndex to pageStories
                    }
            }

        // Merge the list of flows into one chunk
        combine(keyedPageFlows) { arr ->
            arr.toMap()
                .toSortedMap()
                .values
                .flatten()
        }.map { stories: List<DecoratedStory> ->
            // Key the values to the first page, to allow for merging different chunks
            pageIndexes.first() to stories
        }
    }.mergePages()
}

/** Takes a list of values, and returns a page as a subList */
internal fun <T> List<T>.takePage(pageIndex: PageIndex, pageSize: Int): List<T> =
    subList(min(pageIndex * pageSize, size), min((pageIndex + 1) * pageSize, size))

/**
 * Takes in a flow that emits pages with key and current values, and emits a flow with the sorted latest emission per page
 *
 * Eg [1, [A]], [2, [B]], [1, [AA]] emits [A], [A, B], [AA, B]
 */
private fun <T> Flow<Pair<PageIndex, List<T>>>.mergePages(): Flow<List<T>> =
    this.scan(sortedMapOf<PageIndex, List<T>>()) { map, (pageIndex: PageIndex, value: List<T>) ->
        map[pageIndex] = value
        map
    }.map {
        it.values.flatten()
    }