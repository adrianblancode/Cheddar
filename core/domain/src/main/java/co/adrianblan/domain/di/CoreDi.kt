package co.adrianblan.domain.di

import co.adrianblan.common.DefaultDispatcherProvider
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.domain.CustomTabsLauncher
import co.adrianblan.domain.CustomTabsLauncherImpl
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {

    @Singleton
    @Binds
    fun bindCustomTabsLauncher(impl: CustomTabsLauncherImpl): CustomTabsLauncher

    companion object {
        @Singleton
        @Provides
        fun dispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider
    }
}