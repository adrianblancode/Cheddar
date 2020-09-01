package co.adrianblan.storynavigation

import co.adrianblan.storycontent.StoryContentComponent
import co.adrianblan.storyfeed.StoryFeedComponent
import co.adrianblan.storydetail.StoryDetailComponent
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

@Module(
    subcomponents = [
        StoryFeedComponent::class,
        StoryDetailComponent::class,
        StoryContentComponent::class
    ]
)
object StoryNavigationModule

@StoryNavigationScope
@Subcomponent(
    modules = [StoryNavigationModule::class]
)
interface StoryNavigationComponent {
    val storyNavigationNode: StoryNavigationNode

    @Subcomponent.Factory
    interface Factory {
        fun build() : StoryNavigationComponent
    }
}

@Scope
internal annotation class StoryNavigationScope