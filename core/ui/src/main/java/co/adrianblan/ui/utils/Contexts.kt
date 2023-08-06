package co.adrianblan.ui.utils

import android.content.Context
import android.content.res.Configuration

fun Context.isNightModeActive(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}