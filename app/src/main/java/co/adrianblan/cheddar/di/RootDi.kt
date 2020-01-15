package co.adrianblan.cheddar.di

import co.adrianblan.cheddar.MainActivity
import co.adrianblan.stories.StoriesComponent
import co.adrianblan.storydetail.StoryDetailComponent
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

    fun storiesComponentFactory(): StoriesComponent.Factory
    fun storyDetailComponentFactory(): StoryDetailComponent.Factory

    fun inject(activity: MainActivity)
}

@Scope
@Retention
internal annotation class RootScope

@Qualifier
@Retention
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
internal annotation class RootInternal