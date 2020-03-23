package co.adrianblan.cheddar

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.Composable
import androidx.core.net.toUri
import co.adrianblan.cheddar.di.RootInternal
import co.adrianblan.common.ParentScope
import co.adrianblan.ui.Node
import co.adrianblan.ui.RootScreen
import co.adrianblan.ui.StackRouter
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.storyfeed.StoryFeedComponent
import co.adrianblan.storyfeed.StoryFeedNode
import co.adrianblan.storydetail.StoryDetailComponent
import co.adrianblan.storydetail.StoryDetailNode
import javax.inject.Inject

class RootNode
@Inject constructor(
    // TODO remove context
    private val context: Context,
    private val storyFeedComponentBuilder: StoryFeedComponent.Factory,
    private val storyDetailComponentBuilder: StoryDetailComponent.Factory,
    @RootInternal private val parentScope: ParentScope
) : Node, StoryFeedNode.Listener, StoryDetailNode.Listener {

    private val router by lazy {
        StackRouter.of(storyFeedNode)
    }

    private val storyFeedNode: StoryFeedNode by lazy {
        storyFeedComponentBuilder
            .build(
                listener = this,
                parentScope = parentScope
            )
            .storyFeedNode()
    }

    override fun onStoryClicked(storyId: StoryId) {
        router.push(
            storyDetailComponentBuilder
                .build(
                    storyId = storyId,
                    listener = this,
                    parentScope = parentScope
                )
                .storyDetailNode()
        )
    }

    override fun onStoryContentClicked(storyUrl: StoryUrl) {
        CustomTabsIntent.Builder()
            .build()
            .apply {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            .launchUrl(context, storyUrl.url.toUri())
    }

    override fun onStoryDetailFinished() {
        onBackPressed()
    }

    override val composeView = @Composable {
        RootScreen(router)
    }

    override fun onBackPressed(): Boolean =
        router.onBackPressed()

    override fun detach() {}
}