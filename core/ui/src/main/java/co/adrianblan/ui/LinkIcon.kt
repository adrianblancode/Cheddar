package co.adrianblan.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LinkIcon(modifier: Modifier = Modifier) {
    Image(
        imageVector = Icons.Default.Link,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(colorResource(id = R.color.contentMutedSecondary)),
        modifier = modifier
            .graphicsLayer(rotationZ = -45f, scaleX = 0.7f, scaleY = 0.7f)
    )
}

@Preview
@Composable
private fun LinkIconPreview() {
    AppTheme {
        Surface {
            LinkIcon(Modifier.size(120.dp))
        }
    }
}