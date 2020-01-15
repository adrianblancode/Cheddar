package co.adrianblan.cheddar

import co.adrianblan.cheddar.di.RootInternal
import co.adrianblan.common.ParentScope
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

    override fun onStoryDetailFinished() {
        onBackPressed()
    }

    override fun composeView() =
        RootScreen(router)

    override fun onBackPressed(): Boolean =
        router.onBackPressed()

    override fun detach() {}
}