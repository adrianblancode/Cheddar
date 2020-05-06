package co.adrianblan.storyfeed

import co.adrianblan.ui.node.NodeContext
import dagger.BindsInstance
import dagger.Subcomponent
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
            @StoryFeedInternal @BindsInstance nodeContext: NodeContext
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
        nodeContext: NodeContext
    ): StoryFeedNode =
        storyFeedComponentBuilder
            .build(listener, nodeContext)
            .storyFeedNode()
}