package co.adrianblan.storydetail

import android.os.Parcelable
import co.adrianblan.domain.StoryId
import co.adrianblan.matryoshka.node.NodeFactory
import co.adrianblan.matryoshka.node.NodeStore
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
            @BindsInstance storyId: StoryId,
            @BindsInstance @StoryDetailQualifier listener: StoryDetailNode.Listener
        ): StoryDetailComponent
    }
}

@Scope
internal annotation class StoryDetailScope

@Qualifier
internal annotation class StoryDetailQualifier

class StoryDetailNodeProvider
@Inject constructor(
    private val storyDetailComponentBuilder: StoryDetailComponent.Factory
) {
    fun factory(
        storyId: StoryId,
        listener: StoryDetailNode.Listener
    ): NodeFactory<StoryDetailNode> =
        object : NodeFactory<StoryDetailNode> {
            override fun create(savedState: Parcelable?, nodeStore: NodeStore): StoryDetailNode =
                storyDetailComponentBuilder
                    .build(storyId = storyId, listener = listener)
                    .storyDetailNode()
        }
}