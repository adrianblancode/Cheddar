package co.adrianblan.cheddar

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.adrianblan.domain.CustomTabsLauncher
import co.adrianblan.storydetail.ui.StoryDetailViewWrapper
import co.adrianblan.storyfeed.ui.StoryFeedViewWrapper

@Composable
fun Navigation(
    customTabsLauncher: CustomTabsLauncher
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "stories"
    ) {
        composable("stories") {
            StoryFeedViewWrapper(
                onStoryClick = { storyId ->
                    navController.navigate("story/${storyId.id}")
                },
                onStoryContentClick = { storyUrl ->
                    customTabsLauncher.launchUrl(storyUrl.url)
                }
            )
        }
        composable(
            "story/{storyId}",
            arguments = listOf(navArgument("storyId") { type = NavType.LongType })
        ) {
            StoryDetailViewWrapper(
                onStoryContentClick = { storyUrl ->
                    customTabsLauncher.launchUrl(storyUrl.url)
                },
                onCommentUrlClick = { url ->
                    customTabsLauncher.launchUrl(url.toString())
                },
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}