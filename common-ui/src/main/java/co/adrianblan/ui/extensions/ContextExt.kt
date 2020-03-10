package co.adrianblan.ui.extensions

import android.content.res.Configuration
import android.content.res.Resources

fun Resources.isNightModeActive(): Boolean {
    return configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}