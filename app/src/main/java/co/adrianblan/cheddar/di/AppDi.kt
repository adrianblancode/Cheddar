package co.adrianblan.cheddar.di

import android.content.Context
import co.adrianblan.common.DefaultDispatcherProvider
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.hackernews.HackerNewsModule
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.network.NetworkModule
import dagger.*
import javax.inject.Singleton

@Module
object AppModule {
    @Singleton
    @Provides
    fun dispatcherProvider(): DispatcherProvider =
        DefaultDispatcherProvider
}

@Singleton
@Component(modules = [
    AppModule::class,
    NetworkModule::class,
    HackerNewsModule::class
])
interface AppComponent {

    fun context(): Context
    
    fun dispatcherProvider(): DispatcherProvider
    fun hackerNewsRepository(): HackerNewsRepository

    @Component.Factory
    interface Factory {
        fun build(@BindsInstance context: Context): AppComponent
    }
}