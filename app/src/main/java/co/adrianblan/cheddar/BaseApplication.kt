package co.adrianblan.cheddar

import android.app.Application
import android.content.Context
import co.adrianblan.cheddar.di.AppComponent
import co.adrianblan.cheddar.di.DaggerAppComponent
import co.adrianblan.domain.di.DaggerCoreComponent
import timber.log.Timber

class BaseApplication : Application() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val handler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, e ->
            // TODO external error reporting
            Timber.e(e, "Uncaught exception!")
            handler?.uncaughtException(thread, e)
        }

        appComponent = DaggerAppComponent.factory()
            .build(
                DaggerCoreComponent.factory()
                    .build(this)
            )
    }

    companion object {
        fun appComponent(context: Context) =
            (context.applicationContext as BaseApplication).appComponent
    }
}

val Context.appComponent: AppComponent
    get() =
        BaseApplication.appComponent(this)