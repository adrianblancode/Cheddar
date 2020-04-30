package co.adrianblan.ui

import android.view.View
import androidx.compose.*
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import timber.log.Timber

// Alpha for views which have content drawn behind it
const val overInsetAlpha = 0.8f

val InsetsAmbient = ambientOf { Insets.NONE }

// Provides insets to children
@Composable
fun InsetsWrapper(
    view: View,
    content: @Composable() () -> Unit
) {

    val insetState = state<Insets> {
        WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets)
            .systemWindowInsets
    }

    onCommit {

        val listener =
            OnApplyWindowInsetsListener { _, windowInsets ->
                val inset = windowInsets.systemWindowInsets

                insetState.value = inset

                windowInsets
            }

        ViewCompat.setOnApplyWindowInsetsListener(view, listener)
        onDispose { ViewCompat.setOnApplyWindowInsetsListener(view, null) }
    }

    Providers(InsetsAmbient provides insetState.value) {
        content()
    }
}