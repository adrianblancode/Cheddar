package co.adrianblan.boilerplate.di

import android.content.Context
import co.adrianblan.network.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Module
object AppModule {
}

@Singleton
@Component(modules = [
    AppModule::class,
    NetworkModule::class
])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun build(@BindsInstance context: Context): AppComponent
    }
}