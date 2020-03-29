package co.adrianblan.storynavigation

import co.adrianblan.common.ParentScope
import co.adrianblan.storyfeed.StoryFeedComponent
import co.adrianblan.storydetail.StoryDetailComponent
import co.adrianblan.storyfeed.StoryFeedNode
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
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
            @StoryNavigationInternal @BindsInstance scope: CoroutineScope
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
        parentScope: ParentScope
    ): StoryNavigationNode =
        storyNavigationComponentBuilder
            .build(parentScope.createChildScope())
            .storyNavigationNode()
}