package co.adrianblan.cheddar.di

import android.content.Context
import co.adrianblan.cheddar.extensions.CustomTabsLauncherImpl
import co.adrianblan.common.CustomTabsLauncher
import co.adrianblan.common.DefaultDispatcherProvider
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.hackernews.HackerNewsModule
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.HackerNewsRepositoryImpl
import co.adrianblan.network.NetworkModule
import co.adrianblan.webpreview.WebPreviewRepository
import dagger.*
import javax.inject.Singleton

@Module
interface AppModule {

    @Singleton
    @Binds
    fun HackerNewsRepositoryImpl.bindHackerNewsRepository(): HackerNewsRepository

    @Singleton
    @Binds
    fun CustomTabsLauncherImpl.bindCustomTabsLauncher(): CustomTabsLauncher

    companion object {
        @Singleton
        @Provides
        fun dispatcherProvider(): DispatcherProvider =
            DefaultDispatcherProvider
    }
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
    fun customTabsLauncher(): CustomTabsLauncher

    fun hackerNewsRepository(): HackerNewsRepository
    fun webPreviewRepository(): WebPreviewRepository

    @Component.Factory
    interface Factory {
        fun build(@BindsInstance context: Context): AppComponent
    }
}