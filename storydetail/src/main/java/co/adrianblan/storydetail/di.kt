package co.adrianblan.storydetail

import co.adrianblan.hackernews.api.StoryId
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Scope

@StoryDetailScope
@Subcomponent
interface StoryDetailComponent {

    fun storyDetailComposer(): StoryDetailComposer

    @Subcomponent.Factory
    interface Factory {
        fun build(
            @BindsInstance storyId: StoryId,
            @BindsInstance listener: StoryDetailComposer.Listener
        ): StoryDetailComponent
    }
}

@Scope
@Retention
internal annotation class StoryDetailScope