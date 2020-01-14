package co.adrianblan.cheddar.di

import android.content.Context
import co.adrianblan.hackernews.HackerNewsModule
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.network.NetworkModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class,
    HackerNewsModule::class
])
interface AppComponent {

    fun hackerNewsRepository(): HackerNewsRepository

    @Component.Factory
    interface Factory {
        fun build(@BindsInstance context: Context): AppComponent
    }
}