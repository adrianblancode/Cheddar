package co.adrianblan.common.ui

import androidx.compose.Composable

@Composable
fun RootScreen(routerBlock: () -> Router) {
    routerBlock().activeComposer().composeView()
}