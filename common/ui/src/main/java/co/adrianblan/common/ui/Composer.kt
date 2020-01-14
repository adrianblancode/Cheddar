package co.adrianblan.common.ui

import androidx.compose.Composable

interface Composer {
    @Composable
    fun composeView()

    fun onBackPressed() = false
}
