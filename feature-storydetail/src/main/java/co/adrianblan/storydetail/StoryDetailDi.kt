package co.adrianblan.storydetail

import co.adrianblan.domain.StoryId
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Scope

@Module
object StoryDetailModule

@StoryDetailScope
@Subcomponent(modules = [StoryDetailModule::class])
interface StoryDetailComponent {

    val storyDetailNodeFactory: StoryDetailNode.Factory

    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance storyId: StoryId): StoryDetailComponent
    }
}

@Scope
internal annotation class StoryDetailScope

class StoryDetailNodeBuilder
@Inject constructor(
    private val storyDetailComponentBuilder: StoryDetailComponent.Factory
) {
    fun build(
        storyId: StoryId,
        listener: StoryDetailNode.Listener
    ): StoryDetailNode =
        storyDetailComponentBuilder
            .build(storyId)
            .storyDetailNodeFactory
            .create(listener)
}