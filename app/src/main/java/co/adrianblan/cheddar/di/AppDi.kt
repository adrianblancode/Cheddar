package co.adrianblan.cheddar.di

import co.adrianblan.domain.di.CoreComponent
import co.adrianblan.storynavigation.StoryNavigationComponent
import dagger.Component
import dagger.Module
import javax.inject.Scope

@Module(subcomponents = [StoryNavigationComponent::class])
object AppModule

@AppScope
@Component(
    modules = [AppModule::class],
    dependencies = [CoreComponent::class]
)
interface AppComponent {
    val rootComponentFactory: StoryNavigationComponent.Factory

    @Component.Factory
    interface Factory {
        fun build(coreComponent: CoreComponent): AppComponent
    }
}

@Scope
internal annotation class AppScope