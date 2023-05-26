package co.adrianblan.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource

// Link icon that scales to size
@Composable
fun LinkIcon() {
    Image(
        imageVector = Icons.Default.Link,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(colorResource(id = R.color.contentMutedSecondary)),
        modifier = Modifier.fillMaxSize()
            .graphicsLayer(rotationZ = -45f, scaleX = 0.7f, scaleY = 0.7f)
    )
}