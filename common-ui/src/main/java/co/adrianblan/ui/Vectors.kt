package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutSize
import androidx.ui.material.ripple.Ripple


// Shamelessly stolen from Jetnews

@Composable
fun VectorImageButton(
    vector: VectorAsset,
    modifier: Modifier = Modifier.None,
    tint: Color = Color.Transparent,
    onClick: () -> Unit
) {
    Ripple(bounded = false) {
        Clickable(onClick = onClick) {
            VectorImage(vector = vector, modifier = modifier, tint = tint)
        }
    }
}

@Composable
fun VectorImage(
    vector: VectorAsset,
    modifier: Modifier = Modifier.None,
    tint: Color = Color.Transparent
) {
    with(DensityAmbient.current) {
        Container(
            modifier = modifier + LayoutSize(vector.defaultWidth, vector.defaultHeight)
        ) {
            DrawVector(vector, tint)
        }
    }
}
