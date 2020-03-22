package co.adrianblan.ui

import androidx.compose.Composable

interface Node {

    val composeView: @Composable() () -> Unit

    fun detach()

    fun onBackPressed() = false
}
