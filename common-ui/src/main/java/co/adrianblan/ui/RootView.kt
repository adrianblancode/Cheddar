package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface


@Composable
fun RootView(content: @Composable() () -> Unit) {
    Stack {

        val insets = InsetsAmbient.current

        // We don't want to deal with the hassle of left-right insets, so just apply them to all screens
        with(DensityAmbient.current) {
            Box(
                modifier = Modifier.padding(
                    start = insets.left.toDp(),
                    end = insets.right.toDp()
                )
            ) {
                content()
            }
        }

        NavigationBarScrim(modifier = Modifier.gravity(Alignment.BottomCenter))
    }
}

// Compose doesn't seem to play nice with layout flags yet, so let's hack a nav bar scrim
@Composable
fun NavigationBarScrim(
    modifier: Modifier
) {
    val insets = InsetsAmbient.current

    with(DensityAmbient.current) {
        Surface(
            color = MaterialTheme.colors.background.copy(alpha = overInsetAlpha),
            modifier = modifier
                .fillMaxWidth()
                .preferredHeight(insets.bottom.toDp())
        ) {}
    }
}