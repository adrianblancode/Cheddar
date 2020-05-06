package co.adrianblan.storydetail

import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.ui.node.NodeContext
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Scope

@AssistedModule
@Module(includes = [AssistedInject_StoryDetailModule::class])
object StoryDetailModule

@StoryDetailScope
@Subcomponent(modules = [StoryDetailModule::class])
interface StoryDetailComponent {

    fun storyDetailNodeFactory(): StoryDetailNode.Factory

    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance storyId: StoryId): StoryDetailComponent
    }
}

@Scope
@Retention
internal annotation class StoryDetailScope

class StoryDetailNodeBuilder
@Inject constructor(
    private val storyDetailComponentBuilder: StoryDetailComponent.Factory
) {
    fun build(
        storyId: StoryId,
        nodeContext: NodeContext,
        listener: StoryDetailNode.Listener
    ): StoryDetailNode =
        storyDetailComponentBuilder
            .build(storyId)
            .storyDetailNodeFactory()
            .create(nodeContext, listener)
}