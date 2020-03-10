package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.animation.Crossfade
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Stack
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import androidx.ui.unit.px

@Composable
fun RootScreen(router: Router) {
    Crossfade(router.activeComposer()) {
        Stack {
            it.composeView()

            NavigationBarScrim(modifier = LayoutGravity.BottomCenter)
        }
    }
}

// Compose doesn't seem to play nice with layout flags yet, so let's hack a nav bar scrim
@Composable
fun NavigationBarScrim(
    modifier: Modifier
) {
    val insets = InsetsAmbient

    with (DensityAmbient.current) {
        Surface(
            color = MaterialTheme.colors().background.copy(alpha = 0.7f),
            modifier = modifier + LayoutHeight(insets.current.bottom.px.toDp()) + LayoutWidth.Fill
        ) {}
    }
}