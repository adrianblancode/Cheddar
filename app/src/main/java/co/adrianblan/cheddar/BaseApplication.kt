package co.adrianblan.cheddar

import android.app.Application
import timber.log.Timber

class BaseApplication: Application() {

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}