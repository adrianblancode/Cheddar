package co.adrianblan.cheddar

import co.adrianblan.common.ui.Composer
import co.adrianblan.common.ui.RootScreen
import co.adrianblan.common.ui.StackRouter
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.stories.StoriesComponent
import co.adrianblan.stories.StoriesComposer
import co.adrianblan.storydetail.StoryDetailComponent
import co.adrianblan.storydetail.StoryDetailComposer
import javax.inject.Inject

class RootComposer
@Inject constructor(
    private val storiesComponentFactory: StoriesComponent.Factory,
    private val storyDetailComponentFactory: StoryDetailComponent.Factory
) : Composer, StoriesComposer.Listener, StoryDetailComposer.Listener {

    private val router by lazy {
        StackRouter.of(storiesComposer)
    }

    private val storiesComposer: StoriesComposer by lazy {
        storiesComponentFactory
            .build(this)
            .storiesComposer()
    }

    override fun onStoryClicked(storyId: StoryId) {
        router.push(
            storyDetailComponentFactory
                .build(storyId, this)
                .storyDetailComposer()
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