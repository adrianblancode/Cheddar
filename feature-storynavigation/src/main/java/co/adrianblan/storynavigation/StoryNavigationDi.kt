package co.adrianblan.storynavigation

import co.adrianblan.storyfeed.StoryFeedComponent
import co.adrianblan.storydetail.StoryDetailComponent
import co.adrianblan.ui.node.NodeContext
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Scope

@Module(
    subcomponents = [
        StoryFeedComponent::class,
        StoryDetailComponent::class
    ]
)
object RootModule

@StoryNavigationScope
@Subcomponent(
    modules = [RootModule::class]
)
interface StoryNavigationComponent {
    fun storyNavigationNode(): StoryNavigationNode

    @Subcomponent.Factory
    interface Factory {
        fun build(
            @StoryNavigationInternal @BindsInstance nodeContext: NodeContext
        ) : StoryNavigationComponent
    }
}

@Scope
@Retention
internal annotation class StoryNavigationScope

@Qualifier
@Retention
internal annotation class StoryNavigationInternal

class StoryNavigationNodeBuilder
@Inject constructor(
    private val storyNavigationComponentBuilder: StoryNavigationComponent.Factory
) {
    fun build(
        nodeContext: NodeContext
    ): StoryNavigationNode =
        storyNavigationComponentBuilder
            .build(nodeContext)
            .storyNavigationNode()
}