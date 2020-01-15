package co.adrianblan.storydetail

import co.adrianblan.hackernews.api.StoryId
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Scope

@StoryDetailScope
@Subcomponent
abstract class StoryDetailComponent {

    abstract fun storyDetailComposer(): StoryDetailComposer

    @Subcomponent.Factory
    internal interface Factory {
        fun build(
            @BindsInstance storyId: StoryId,
            @BindsInstance listener: StoryDetailComposer.Listener
        ): StoryDetailComponent
    }
}

class StoryDetailBuilder
@Inject internal constructor(
    private val storyDetailComponentFactory: StoryDetailComponent.Factory
) {
    fun build(
        storyId: StoryId,
        listener: StoryDetailComposer.Listener
    ): StoryDetailComposer =
        storyDetailComponentFactory
            .build(
                storyId = storyId,
                listener = listener
            )
            .storyDetailComposer()
}

@Scope
@Retention
internal annotation class StoryDetailScope