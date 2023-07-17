package co.adrianblan.ui

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.Transformation
import kotlin.math.min

private const val minBitmapSizePx = 48

/** Takes an image url and loads it with Picasso */
@Composable
fun UrlImage(
    imageUrl: String,
    height: Dp = 80.dp,
    width: Dp = 80.dp,
    fallbackIcon: @Composable (() -> Unit)? = null,
) {

    val density = LocalDensity.current
    val context = LocalContext.current

    val targetWidthPx = remember(density) { with(density) { width.roundToPx() } }
    val targetHeightPx = remember(density) { with(density) { height.roundToPx() } }

    val pixelatedTransformation = remember(targetWidthPx, targetHeightPx) {
        PixelatedTransformation(targetWidthPx, targetHeightPx)
    }

    val imageRequest = remember(context, pixelatedTransformation) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .transformations(CropSquareTransformation, pixelatedTransformation)
            .build()
    }

    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = null,
        loading = {
            Box(modifier = Modifier.fillMaxSize()) {
                ShimmerView()
                fallbackIcon?.invoke()
            }
        },
        success = {
            SubcomposeAsyncImageContent()
        },
        error = {
            fallbackIcon?.invoke()
        }
    )
}

object CropSquareTransformation : Transformation {
    override val cacheKey: String = "square"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val minSize = min(input.width, input.height)
        val x = (input.width - minSize) / 2
        val y = (input.height - minSize) / 2
        val result = Bitmap.createBitmap(input, x, y, minSize, minSize)
        if (result != input) {
            input.recycle()
        }
        return result
    }
}

// Really small images are loaded as pixelated to not introduce blurriness through filtering
class PixelatedTransformation(private val targetWidthPx: Int, private val targetHeightPx: Int) :
    Transformation {
    override val cacheKey: String = "pixelated"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val isPixelatedIcon =
            input.height <= minBitmapSizePx || input.width <= minBitmapSizePx

        val result = Bitmap.createScaledBitmap(
            input,
            targetWidthPx,
            targetHeightPx,
            !isPixelatedIcon
        )

        if (result != input) {
            input.recycle()
        }
        return result
    }
}