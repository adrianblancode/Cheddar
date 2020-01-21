package co.adrianblan.ui

import androidx.compose.Composable

@Composable
fun RootScreen(router: Router) {
    router.activeComposer().composeView()
}