package co.adrianblan.storyfeed

import dagger.Module
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Scope

@Module
object StoryFeedModule

@StoryFeedScope
@Subcomponent(modules = [StoryFeedModule::class])
interface StoryFeedComponent {

    val storyFeedNodeFactory: StoryFeedNode.Factory

    @Subcomponent.Factory
    interface Factory {
        fun build(): StoryFeedComponent
    }
}

@Scope
internal annotation class StoryFeedScope

class StoryFeedNodeBuilder
@Inject constructor(
    private val storyFeedComponentBuilder: StoryFeedComponent.Factory
) {
    fun build(
        listener: StoryFeedNode.Listener
    ): StoryFeedNode =
        storyFeedComponentBuilder
            .build()
            .storyFeedNodeFactory
            .create(listener)
}