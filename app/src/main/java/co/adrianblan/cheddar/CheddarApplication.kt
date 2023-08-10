package co.adrianblan.cheddar

import android.app.Application
import co.adrianblan.ui.coil.CoilImageLoaderFactory
import coil.Coil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CheddarApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Coil.setImageLoader(CoilImageLoaderFactory(this))
    }
}