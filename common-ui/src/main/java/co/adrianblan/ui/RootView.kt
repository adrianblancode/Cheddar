package co.adrianblan.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity


@Composable
fun RootView(content: @Composable() () -> Unit) {
    Box {

        val insets = LocalInsets.current

        // We don't want to deal with the hassle of left-right insets, so just apply them to all screens
        with(LocalDensity.current) {
            Box(
                modifier = Modifier.padding(
                    start = insets.left.toDp(),
                    end = insets.right.toDp()
                )
            ) {
                content()
            }
        }

        NavigationBarScrim(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

// Compose doesn't seem to play nice with layout flags yet, so let's hack a nav bar scrim
@Composable
fun NavigationBarScrim(
    modifier: Modifier
) {
    val insets = LocalInsets.current

    with(LocalDensity.current) {
        Surface(
            color = MaterialTheme.colors.background.copy(alpha = overInsetAlpha),
            modifier = modifier
                .fillMaxWidth()
                .height(insets.bottom.toDp())
        ) {}
    }
}