package co.adrianblan.ui.utils

import android.content.res.Configuration
import android.content.res.Resources

fun Resources.isNightModeActive(): Boolean {
    return configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

enum class Orientation {
    PORTRAIT, LANDSCAPE
}

fun Resources.orientation(): Orientation {
    return if ((configuration.orientation and Configuration.ORIENTATION_LANDSCAPE) != 0) Orientation.LANDSCAPE
    else Orientation.PORTRAIT
}