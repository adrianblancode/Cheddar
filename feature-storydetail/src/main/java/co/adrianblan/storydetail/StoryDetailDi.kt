package co.adrianblan.storydetail

import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.ui.node.NodeContext
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Scope

@StoryDetailScope
@Subcomponent
interface StoryDetailComponent {

    fun storyDetailNode(): StoryDetailNode

    @Subcomponent.Factory
    interface Factory {
        fun build(
            @StoryDetailInternal @BindsInstance storyId: StoryId,
            @StoryDetailInternal @BindsInstance listener: StoryDetailNode.Listener,
            @StoryDetailInternal @BindsInstance nodeContext: NodeContext
        ): StoryDetailComponent
    }
}

@Scope
@Retention
internal annotation class StoryDetailScope

@Qualifier
@Retention
internal annotation class StoryDetailInternal

class StoryDetailNodeBuilder
@Inject constructor(
    private val storyDetailComponentBuilder: StoryDetailComponent.Factory
) {
    fun build(
        storyId: StoryId,
        listener: StoryDetailNode.Listener,
        nodeContext: NodeContext
    ): StoryDetailNode =
        storyDetailComponentBuilder
            .build(storyId, listener, nodeContext)
            .storyDetailNode()
}