package co.adrianblan.cheddar

import co.adrianblan.common.ui.Composer
import co.adrianblan.common.ui.RootScreen
import co.adrianblan.common.ui.StackRouter
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.stories.StoriesComposer
import co.adrianblan.stories.StoriesBuilder
import co.adrianblan.storydetail.StoryDetailComposer
import co.adrianblan.storydetail.StoryDetailBuilder
import javax.inject.Inject

class RootComposer
@Inject constructor(
    private val storiesBuilder: StoriesBuilder,
    private val storyDetailBuilder: StoryDetailBuilder
) : Composer, StoriesComposer.Listener, StoryDetailComposer.Listener {

    private val router by lazy {
        StackRouter.of(storiesComposer)
    }

    private val storiesComposer: StoriesComposer by lazy {
        storiesBuilder
            .build(this)
    }

    override fun onStoryClicked(storyId: StoryId) {
        router.push(
            storyDetailBuilder
                .build(
                    storyId = storyId,
                    listener = this
                )
        )
    }

    override fun onStoryDetailFinished() {
        onBackPressed()
    }

    override fun composeView() =
        RootScreen { router }

    override fun onBackPressed(): Boolean =
        router.onBackPressed()
}