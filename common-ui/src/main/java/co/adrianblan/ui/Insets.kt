package co.adrianblan.ui

import android.view.View
import androidx.compose.runtime.*
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Alpha for views which have content drawn behind it
const val overInsetAlpha = 0.8f

val InsetsAmbient = ambientOf { Insets.NONE }

// Provides insets to children
@Composable
fun InsetsWrapper(
    view: View,
    content: @Composable () -> Unit
) {

    var insets by state<Insets> {
        view.rootWindowInsets?.let {
            WindowInsetsCompat.toWindowInsetsCompat(it)
                .systemWindowInsets
        } ?: Insets.NONE
    }

    onCommit {

        val listener =
            OnApplyWindowInsetsListener { _, windowInsets ->
                val inset = windowInsets.systemWindowInsets

                insets = inset

                windowInsets
            }

        ViewCompat.setOnApplyWindowInsetsListener(view, listener)
        onDispose { ViewCompat.setOnApplyWindowInsetsListener(view, null) }
    }

    Providers(InsetsAmbient provides insets) {
        content()
    }
}