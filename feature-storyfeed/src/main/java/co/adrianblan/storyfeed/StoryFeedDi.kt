package co.adrianblan.storyfeed

import co.adrianblan.common.ParentScope
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Qualifier
import javax.inject.Scope

@StoryFeedScope
@Subcomponent
interface StoryFeedComponent {

    fun storyFeedComposer(): StoryFeedComposer

    @Subcomponent.Factory
    interface Factory {
        fun build(
            @StoryFeedInternal @BindsInstance listener: StoryFeedComposer.Listener,
            @StoryFeedInternal @BindsInstance parentScope: ParentScope
        ): StoryFeedComponent
    }
}

@Scope
@Retention
internal annotation class StoryFeedScope

@Qualifier
@Retention
internal annotation class StoryFeedInternal