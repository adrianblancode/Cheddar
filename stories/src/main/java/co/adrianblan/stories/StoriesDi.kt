package co.adrianblan.stories

import co.adrianblan.common.ParentScope
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Scope

@StoriesScope
@Subcomponent
interface StoriesComponent {

    fun storiesComposer(): StoriesComposer

    @Subcomponent.Factory
    interface Factory {
        fun build(
            @StoriesInternal @BindsInstance listener: StoriesComposer.Listener,
            @StoriesInternal @BindsInstance parentScope: ParentScope
        ): StoriesComponent
    }
}

@Scope
@Retention
internal annotation class StoriesScope

@Qualifier
@Retention
internal annotation class StoriesInternal