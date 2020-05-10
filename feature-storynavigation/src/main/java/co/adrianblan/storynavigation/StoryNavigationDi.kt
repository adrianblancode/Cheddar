package co.adrianblan.storynavigation

import co.adrianblan.storyfeed.StoryFeedComponent
import co.adrianblan.storydetail.StoryDetailComponent
import co.adrianblan.matryoshka.NodeContext
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Scope

@AssistedModule
@Module(
    subcomponents = [
        StoryFeedComponent::class,
        StoryDetailComponent::class
    ],
    includes = [AssistedInject_StoryNavigationModule::class]
)
object StoryNavigationModule

@StoryNavigationScope
@Subcomponent(
    modules = [StoryNavigationModule::class]
)
interface StoryNavigationComponent {
    fun storyNavigationNodeFactory(): StoryNavigationNode.Factory

    @Subcomponent.Factory
    interface Factory {
        fun build(
        ) : StoryNavigationComponent
    }
}

@Scope
@Retention
internal annotation class StoryNavigationScope

class StoryNavigationNodeBuilder
@Inject constructor(
    private val storyNavigationComponentBuilder: StoryNavigationComponent.Factory
) {
    fun build(
        nodeContext: NodeContext
    ): StoryNavigationNode =
        storyNavigationComponentBuilder
            .build()
            .storyNavigationNodeFactory()
            .create(nodeContext)
}