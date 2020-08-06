package co.adrianblan.ui

import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.DensityAmbient


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