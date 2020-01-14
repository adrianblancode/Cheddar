package co.adrianblan.cheddar

import android.app.Application
import android.content.Context
import co.adrianblan.cheddar.di.AppComponent
import co.adrianblan.cheddar.di.DaggerAppComponent
import timber.log.Timber

class BaseApplication: Application() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appComponent = DaggerAppComponent.factory()
            .build(this)
    }

    companion object {
        fun getAppComponent(context: Context) =
            (context.applicationContext as BaseApplication).appComponent
    }
}