package co.adrianblan.boilerplate.di

import android.content.Context
import co.adrianblan.boilerplate.BaseApplication
import co.adrianblan.network.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Module
object AppModule {
}

@Singleton
@Component(modules = [
    AppModule::class,
    NetworkModule::class,
    AndroidSupportInjectionModule::class
])
interface AppComponent : AndroidInjector<BaseApplication> {
    @Component.Factory
    interface Factory {
        fun build(@BindsInstance context: Context): AppComponent
    }
}