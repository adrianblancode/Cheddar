package co.adrianblan.cheddar.di

import android.content.Context
import co.adrianblan.hackernews.HackerNewsModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    HackerNewsModule::class
])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun build(@BindsInstance context: Context): AppComponent
    }
}