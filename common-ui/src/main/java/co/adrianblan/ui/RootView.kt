package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.animation.Crossfade

@Composable
fun RootScreen(router: Router) {
    Crossfade(router.activeComposer()) {
        it.composeView()
    }
}