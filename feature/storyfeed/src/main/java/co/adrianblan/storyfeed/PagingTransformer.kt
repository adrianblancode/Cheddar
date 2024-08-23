package co.adrianblan.storyfeed

import androidx.annotation.VisibleForTesting
import co.adrianblan.domain.DecoratedStory
import co.adrianblan.model.StoryId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlin.math.min

typealias PageIndex = Int
typealias PageStories = Pair<Int, List<DecoratedStory>>

/**
 * Takes a source of page index to story ids,
 * and a source story id to flow of story,
 * returns a flow of content for all pages.
 */
internal fun StateFlow<PageIndex>.observePages(
    pageStoryIdsSource: (PageIndex) -> List<StoryId>,
    storyFlowSource: (StoryId) -> Flow<DecoratedStory>
): Flow<List<DecoratedStory>> {
    return transformPaginationChunks()
        .loadPageChunkContent { pageIndex ->
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
@VisibleForTesting
internal fun StateFlow<PageIndex>.transformPaginationChunks(): Flow<List<PageIndex>> {

    val pageIndexFlow = this

    // Flow of indexes to a chunk of pages to load
    // Since we don't have to start at zero, we emit chunks of pages
    return flow {
        var numLoadedPages = 0

        // StateFlow can skip values, so emit intermediate pages
        pageIndexFlow.collect { pageIndex ->
            emit((numLoadedPages..pageIndex).toList())
            numLoadedPages = pageIndex + 1
        }
    }
}

private fun Flow<List<PageIndex>>.loadPageChunkContent(
    pageFlowSource: (PageIndex) -> Flow<List<DecoratedStory>>
): Flow<List<DecoratedStory>> {
    val pageChunkFlow = this

    val unmergedPagesFlow: Flow<Pair<PageIndex, List<DecoratedStory>>> = channelFlow {
        val channelScope = this
        pageChunkFlow.collect { pagesInChunk: List<Int> ->
            // For each page chunk, launch a new coroutine to collect the content
            // This is so that we immediately start loading new chunks,
            // without having to wait for previous ones to finish
            launch {

                // Flow of stories with key to their page
                val storiesInChunkFlow: List<Flow<PageStories>> =
                    pagesInChunk.map { pageIndex ->
                        pageFlowSource(pageIndex)
                            .map { pageStories ->
                                pageIndex to pageStories
                            }
                    }

                // Combine loading of all pages
                combine(storiesInChunkFlow) { arr ->
                    arr.toMap()
                        .toSortedMap()
                        .values
                        .flatten()
                }
                    .map { stories: List<DecoratedStory> ->
                        // Key the stories to the first page in the chunk, to allow for merging different chunks
                        pagesInChunk.first() to stories
                    }
                    .collect {
                        channelScope.trySend(it)
                    }
            }
        }
    }

    return unmergedPagesFlow.mergePages()
}

/** Takes a list of values, and returns a page as a subList */
internal fun <T> List<T>.takePage(pageIndex: PageIndex, pageSize: Int): List<T> =
    subList(min(pageIndex * pageSize, size), min((pageIndex + 1) * pageSize, size))

/**
 * Takes in a flow that emits pages with key and current values, and emits a flow with the sorted latest emission per page
 *
 * Eg [1, [A]], [2, [B]], [1, [AA]] emits [A], [A, B], [AA, B]
 */
@VisibleForTesting
internal fun <T> Flow<Pair<PageIndex, List<T>>>.mergePages(): Flow<List<T>> =
    this.scan(sortedMapOf<PageIndex, List<T>>()) { map, (pageIndex: PageIndex, value: List<T>) ->
        map[pageIndex] = value
        map
    }
        // Drop initial empty list
        .drop(1)
        .map {
            it.values.flatten()
        }
