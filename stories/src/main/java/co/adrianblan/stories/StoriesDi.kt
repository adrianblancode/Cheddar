package co.adrianblan.stories

import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Scope

@StoriesScope
@Subcomponent
abstract class StoriesComponent {

    abstract fun storiesComposer(): StoriesComposer

    @Subcomponent.Factory
    internal interface Factory {
        fun build(@BindsInstance listener: StoriesComposer.Listener): StoriesComponent
    }
}

class StoriesBuilder
@Inject internal constructor(
    private val storiesComponentFactory: StoriesComponent.Factory
) {
    fun build(listener: StoriesComposer.Listener): StoriesComposer =
        storiesComponentFactory
            .build(listener)
            .storiesComposer()
}

@Scope
@Retention
internal annotation class StoriesScope