package co.adrianblan.storydetail

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryUrl
import co.adrianblan.storydetail.ui.StoryDetailRoute

internal const val storyIdArg = "storyId"

internal class StoryDetailArgs(val storyId: StoryId) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(StoryId(savedStateHandle[storyIdArg]!!))
}

@VisibleForTesting
internal fun StoryDetailArgs.toSavedStateHandle() =
    SavedStateHandle(mapOf(storyIdArg to this.storyId.id))

fun NavController.navigateToTopic(storyId: StoryId) {
    this.navigate("story/${storyId.id}") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.storyDetailScreen(
    onStoryContentClick: (StoryUrl) -> Unit,
    onCommentUrlClick: (Uri) -> Unit,
    onBackPressed: () -> Unit
) {
    composable(
        route = "story/{$storyIdArg}",
        arguments = listOf(
            navArgument(storyIdArg) { type = NavType.LongType }
        )
    ) {
        StoryDetailRoute(
            onStoryContentClick = onStoryContentClick,
            onCommentUrlClick = onCommentUrlClick,
            onBackPressed = onBackPressed
        )
    }
}
