package co.adrianblan.ui.extensions

import androidx.ui.unit.Px

fun lerp(start: Float, end: Float, fraction: Float): Float =
    start + (end - start) * fraction.coerceIn(0f, 1f)

fun lerp(start: Px, end: Px, fraction: Float): Px =
    start + (end - start) * fraction.coerceIn(0f, 1f)
