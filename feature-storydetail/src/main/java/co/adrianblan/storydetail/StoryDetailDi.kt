package co.adrianblan.storydetail

import co.adrianblan.common.ParentScope
import co.adrianblan.hackernews.api.StoryId
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Qualifier
import javax.inject.Scope

@StoryDetailScope
@Subcomponent
interface StoryDetailComponent {

    fun storyDetailComposer(): StoryDetailComposer

    @Subcomponent.Factory
    interface Factory {
        fun build(
            @StoryDetailInternal @BindsInstance storyId: StoryId,
            @StoryDetailInternal @BindsInstance listener: StoryDetailComposer.Listener,
            @StoryDetailInternal @BindsInstance parentScope: ParentScope
        ): StoryDetailComponent
    }
}

@Scope
@Retention
internal annotation class StoryDetailScope

@Qualifier
@Retention
internal annotation class StoryDetailInternal