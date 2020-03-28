package co.adrianblan.storydetail

import co.adrianblan.common.ParentScope
import co.adrianblan.hackernews.api.StoryId
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
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
            @StoryDetailInternal @BindsInstance scope: CoroutineScope
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
        parentScope: ParentScope
    ): StoryDetailNode =
        storyDetailComponentBuilder
            .build(storyId, listener, parentScope.createChildScope())
            .storyDetailNode()
}