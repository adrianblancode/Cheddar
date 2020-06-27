package co.adrianblan.storyfeed

import android.os.Parcelable
import co.adrianblan.matryoshka.node.NodeFactory
import co.adrianblan.matryoshka.node.NodeStore
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Scope

@AssistedModule
@Module(includes = [AssistedInject_StoryFeedModule::class])
object StoryFeedModule

@StoryFeedScope
@Subcomponent(modules = [StoryFeedModule::class])
interface StoryFeedComponent {

    fun storyFeedNode(): StoryFeedNode

    @Subcomponent.Factory
    interface Factory {
        fun build(
            @BindsInstance @StoryFeedQualifier listener: StoryFeedNode.Listener
        ): StoryFeedComponent
    }
}

@Scope
internal annotation class StoryFeedScope

@Qualifier
internal annotation class StoryFeedQualifier

class StoryFeedNodeProvider
@Inject constructor(
    private val storyFeedComponentBuilder: StoryFeedComponent.Factory
) {
    fun factory(
        listener: StoryFeedNode.Listener
    ) = object : NodeFactory<StoryFeedNode> {
        override fun create(savedState: Parcelable?, nodeStore: NodeStore): StoryFeedNode =
            storyFeedComponentBuilder
                .build(listener)
                .storyFeedNode()
    }

}