package co.adrianblan.ui

import androidx.compose.Composable

interface Composer {
    @Composable
    fun composeView()

    fun detach()

    fun onBackPressed() = false
}
