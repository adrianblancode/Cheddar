package co.adrianblan.storyfeed

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryUrl
import co.adrianblan.storyfeed.ui.StoryFeedRoute

const val storyFeedRoute = "stories"

fun NavController.navigateStoryFeed() {
    this.navigate(storyFeedRoute) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.storyFeedScreen(
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
) {
    composable(storyFeedRoute) {
        StoryFeedRoute(
            onStoryClick = onStoryClick,
            onStoryContentClick = onStoryContentClick
        )
    }
}