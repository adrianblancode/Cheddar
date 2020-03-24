package co.adrianblan.ui.node

import androidx.compose.Composable

/** A node is a composable unit of UI and business logic */
interface Node {

    val composeView: @Composable() () -> Unit

    fun detach()

    fun onBackPressed() = false
}
