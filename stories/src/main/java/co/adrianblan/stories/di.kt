package co.adrianblan.stories

import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Scope

@StoriesScope
@Subcomponent
interface StoriesComponent {

    fun storiesComposer(): StoriesComposer

    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance listener: StoriesComposer.Listener): StoriesComponent
    }
}

@Scope
@Retention
internal annotation class StoriesScope