package co.adrianblan.ui

import android.view.View
import androidx.compose.*
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat

val InsetsAmbient = ambientOf { Insets.NONE }

// Provides insets to children
@Composable
fun InsetsWrapper(
    view: View,
    children: @Composable() () -> Unit
) {

    val insetState = state { Insets.NONE }

    val listener: OnApplyWindowInsetsListener =
        remember {
            OnApplyWindowInsetsListener { _, windowInsets ->
                val inset = windowInsets.systemWindowInsets

                insetState.value = inset

                windowInsets
            }
        }

    onCommit(view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, listener)
        onDispose { ViewCompat.setOnApplyWindowInsetsListener(view, null) }
    }

    Providers(InsetsAmbient provides insetState.value) {
        children()
    }
}