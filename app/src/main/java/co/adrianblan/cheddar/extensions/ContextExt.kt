package co.adrianblan.cheddar.extensions

import android.content.Context
import co.adrianblan.cheddar.BaseApplication
import co.adrianblan.cheddar.di.AppComponent

val Context.appComponent: AppComponent get() =
    BaseApplication.getAppComponent(this)