package co.adrianblan.boilerplate

import co.adrianblan.boilerplate.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class BaseApplication: DaggerApplication() {

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<BaseApplication> =
        DaggerAppComponent.factory()
            .build(this)
}