package co.adrianblan.ui

import androidx.ui.unit.TextUnit

// Interpolates between two text units in Sp
fun lerp(start: TextUnit, end: TextUnit, fraction: Float): TextUnit {
    val size = start.value + (end.value - start.value) * fraction.coerceIn(0f, 1f)
    return TextUnit.Sp(size)
}

