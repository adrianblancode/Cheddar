package co.adrianblan.cheddar.di

import co.adrianblan.cheddar.RootActivity
import dagger.Component

@ActivityScope
@Component(
    dependencies = [AppComponent::class]
)
interface RootComponent {
    fun inject(activity: RootActivity)
}