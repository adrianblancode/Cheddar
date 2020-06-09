package co.adrianblan.cheddar.di

import co.adrianblan.storynavigation.StoryNavigationComponent
import co.adrianblan.storynavigation.StoryNavigationNode
import dagger.Component
import dagger.Module
import javax.inject.Scope

@Module(subcomponents = [StoryNavigationComponent::class])
object RootModule

@RootScope
@Component(
    modules = [RootModule::class],
    dependencies = [AppComponent::class]
)
interface RootComponent {
    fun rootComponentFactory(): StoryNavigationComponent.Factory

    @Component.Factory
    interface Factory {
        fun build(appComponent: AppComponent): RootComponent
    }
}

@Scope
internal annotation class RootScope