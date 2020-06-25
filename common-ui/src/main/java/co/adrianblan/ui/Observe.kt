package co.adrianblan.ui

import androidx.compose.Composable

@Composable
fun Observe(body: @Composable() () -> Unit) = body()