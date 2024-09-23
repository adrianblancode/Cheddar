package co.adrianblan.cheddar

import co.adrianblan.ui.coil.CoilImageLoaderFactory
import coil.Coil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

//import androidx.multidex.MultiDexApplication
//@HiltAndroidApp(MultiDexApplication::class)
//class CheddarApplication : Hilt_CheddarApplication() {
//class CheddarApplication : Hilt_CheddarApplication() {
@HiltAndroidApp
class CheddarApplication : Hilt_CheddarApplication() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Coil.setImageLoader(CoilImageLoaderFactory(this))
    }
}
