package co.adrianblan.ui

import android.view.View
import androidx.compose.runtime.*
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Alpha for views which have content drawn behind it
const val overInsetAlpha = 0.8f

val LocalInsets = compositionLocalOf { Insets.NONE }

// Provides insets to children
@Composable
fun InsetsWrapper(
    view: View,
    content: @Composable () -> Unit
) {

    val insets = remember {
        mutableStateOf<Insets>(
            view.rootWindowInsets?.let {
                WindowInsetsCompat.toWindowInsetsCompat(it)
                    .systemWindowInsets
            } ?: Insets.NONE
        )
    }

    DisposableEffect(Unit) {

        val listener =
            OnApplyWindowInsetsListener { _, windowInsets ->
                val inset = windowInsets.systemWindowInsets
                insets.value = inset
                windowInsets
            }

        ViewCompat.setOnApplyWindowInsetsListener(view, listener)

        onDispose { ViewCompat.setOnApplyWindowInsetsListener(view, null) }
    }

    CompositionLocalProvider(LocalInsets provides insets.value) {
        content()
    }
}