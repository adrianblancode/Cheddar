package co.adrianblan.cheddar.di

import co.adrianblan.cheddar.RootComposer
import co.adrianblan.common.ParentScope
import co.adrianblan.stories.StoriesComponent
import co.adrianblan.storydetail.StoryDetailComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Qualifier
import javax.inject.Scope

@Module(
    subcomponents = [
        StoriesComponent::class,
        StoryDetailComponent::class
    ]
)
object RootModule

@RootScope
@Component(
    modules = [RootModule::class],
    dependencies = [AppComponent::class]
)
interface RootComponent {
    fun rootComposer(): RootComposer

    @Component.Factory
    interface Factory {
        fun build(
            @RootInternal @BindsInstance parentScope: ParentScope,
            appComponent: AppComponent
        ) : RootComponent
    }
}

@Scope
@Retention
internal annotation class RootScope

@Qualifier
@Retention
internal annotation class RootInternal