package co.adrianblan.core.di

import android.content.Context
import co.adrianblan.common.CustomTabsLauncher
import co.adrianblan.common.DefaultDispatcherProvider
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.core.StoryPreviewUseCase
import co.adrianblan.core.utils.CustomTabsLauncherImpl
import co.adrianblan.hackernews.HackerNewsModule
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.HackerNewsRepositoryImpl
import co.adrianblan.network.NetworkModule
import co.adrianblan.webpreview.WebPreviewRepository
import dagger.*
import javax.inject.Singleton

@Module
interface CoreModule {

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
@Component(
    modules = [
        CoreModule::class,
        UseCaseModule::class,
        NetworkModule::class,
        HackerNewsModule::class
    ]
)
interface CoreComponent {

    fun context(): Context

    fun dispatcherProvider(): DispatcherProvider
    fun customTabsLauncher(): CustomTabsLauncher

    fun hackerNewsRepository(): HackerNewsRepository
    fun webPreviewRepository(): WebPreviewRepository

    fun storyPreviewUseCase(): StoryPreviewUseCase

    @Component.Factory
    interface Factory {
        fun build(@BindsInstance context: Context): CoreComponent
    }
}