package co.adrianblan.storyfeed

import co.adrianblan.common.ParentScope
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Scope

@StoryFeedScope
@Subcomponent
interface StoryFeedComponent {

    fun storyFeedNode(): StoryFeedNode

    @Subcomponent.Factory
    interface Factory {
        fun build(
            @StoryFeedInternal @BindsInstance listener: StoryFeedNode.Listener,
            @StoryFeedInternal @BindsInstance scope: CoroutineScope
        ): StoryFeedComponent
    }
}

@Scope
@Retention
internal annotation class StoryFeedScope

@Qualifier
@Retention
internal annotation class StoryFeedInternal


class StoryFeedNodeBuilder
@Inject constructor(
    private val storyFeedComponentBuilder: StoryFeedComponent.Factory
) {
    fun build(
        listener: StoryFeedNode.Listener,
        parentScope: ParentScope
    ): StoryFeedNode =
        storyFeedComponentBuilder
            .build(listener, parentScope.createChildScope())
            .storyFeedNode()
}