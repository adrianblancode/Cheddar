package co.adrianblan.storycontent

import android.net.Uri
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Scope

@Module
object StoryFeedModule

@StoryContentScope
@Subcomponent(modules = [StoryFeedModule::class])
interface StoryContentComponent {
    val storyContentNode: StoryContentNode

    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance url: Uri) : StoryContentComponent
    }
}

@Scope
internal annotation class StoryContentScope

class StoryContentNodeBuilder
@Inject constructor(
    private val storyContentComponentBuilder: StoryContentComponent.Factory
) {
    fun build(
        url: Uri,
    ): StoryContentNode =
        storyContentComponentBuilder
            .build(url)
            .storyContentNode
}