package co.adrianblan.cheddar

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import co.adrianblan.domain.CustomTabsLauncher
import co.adrianblan.storydetail.navigateToTopic
import co.adrianblan.storydetail.storyDetailScreen
import co.adrianblan.storyfeed.storyFeedRoute
import co.adrianblan.storyfeed.storyFeedScreen

@Composable
fun Navigation(
    customTabsLauncher: CustomTabsLauncher
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = storyFeedRoute,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                initialOffset = { it }
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                targetOffset = { -it / 2 }
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                initialOffset = { -it / 2 }
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                targetOffset = { it }
            )
        },
    ) {

        storyFeedScreen(
            onStoryClick = navController::navigateToTopic,
            onStoryContentClick = { storyUrl ->
                customTabsLauncher.launchUrl(storyUrl.url)
            }
        )
        storyDetailScreen(
            onStoryContentClick = { storyUrl ->
                customTabsLauncher.launchUrl(storyUrl.url)
            },
            onCommentUrlClick = { uri ->
                customTabsLauncher.launchUrl(uri.toString())
            },
            onBackPressed = navController::popBackStack
        )
    }
}