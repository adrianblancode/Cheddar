package co.adrianblan.ui

import androidx.compose.Composable

interface Composer {

    val composeView: @Composable() () -> Unit

    fun detach()

    fun onBackPressed() = false
}
