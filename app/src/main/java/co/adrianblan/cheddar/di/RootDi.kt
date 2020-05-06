package co.adrianblan.cheddar.di

import co.adrianblan.storynavigation.StoryNavigationComponent
import co.adrianblan.storynavigation.StoryNavigationNodeBuilder
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
    fun rootNodeBuilder(): StoryNavigationNodeBuilder

    @Component.Factory
    interface Factory {
        fun build(appComponent: AppComponent): RootComponent
    }
}

@Scope
@Retention
internal annotation class RootScope