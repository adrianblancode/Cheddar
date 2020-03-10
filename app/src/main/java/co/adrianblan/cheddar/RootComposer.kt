package co.adrianblan.cheddar

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import co.adrianblan.cheddar.di.RootInternal
import co.adrianblan.common.ParentScope
import co.adrianblan.ui.Composer
import co.adrianblan.ui.RootScreen
import co.adrianblan.ui.StackRouter
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.stories.StoriesComponent
import co.adrianblan.stories.StoriesComposer
import co.adrianblan.storydetail.StoryDetailComponent
import co.adrianblan.storydetail.StoryDetailComposer
import javax.inject.Inject

class RootComposer
@Inject constructor(
    // TODO remove context
    private val context: Context,
    private val storiesComponentBuilder: StoriesComponent.Factory,
    private val storyDetailComponentBuilder: StoryDetailComponent.Factory,
    @RootInternal private val parentScope: ParentScope
) : Composer, StoriesComposer.Listener, StoryDetailComposer.Listener {

    private val router by lazy {
        StackRouter.of(storiesComposer)
    }

    private val storiesComposer: StoriesComposer by lazy {
        storiesComponentBuilder
            .build(
                listener = this,
                parentScope = parentScope
            )
            .storiesComposer()
    }

    override fun onStoryClicked(storyId: StoryId) {
        router.push(
            storyDetailComponentBuilder
                .build(
                    storyId = storyId,
                    listener = this,
                    parentScope = parentScope
                )
                .storyDetailComposer()
        )
    }

    override fun onStoryContentClicked(storyContentUrl: String) {
        CustomTabsIntent.Builder()
            .build()
            .apply {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            .launchUrl(context, storyContentUrl.toUri())
    }

    override fun onStoryDetailFinished() {
        onBackPressed()
    }

    override fun composeView() =
        RootScreen(router)

    override fun onBackPressed(): Boolean =
        router.onBackPressed()

    override fun detach() {}
}