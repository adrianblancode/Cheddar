package co.adrianblan.cheddar.di

import co.adrianblan.cheddar.RootComposer
import co.adrianblan.stories.StoriesComponent
import co.adrianblan.storydetail.StoryDetailComponent
import dagger.Component
import dagger.Module
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
}

@Scope
@Retention
internal annotation class RootScope