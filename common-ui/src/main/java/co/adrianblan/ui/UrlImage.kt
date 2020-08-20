package co.adrianblan.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.squareup.picasso.Transformation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.yield
import kotlin.coroutines.resume
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

    val targetWidthPx = with(DensityAmbient.current) { remember { width.toIntPx() } }
    val targetHeightPx = with(DensityAmbient.current) { remember { height.toIntPx() } }

    val imageState = remember(imageUrl) { mutableStateOf<ImageState>(ImageState.Loading) }

    launchInComposition(imageUrl) {
        imageState.value = loadImage(imageUrl, targetWidthPx, targetHeightPx)
    }

    DrawImageState(state = imageState.value, fallbackIcon = fallbackIcon)
}

private suspend fun loadImage(
    imageUrl: String,
    targetWidthPx: Int,
    targetHeightPx: Int
): ImageState =
    suspendCancellableCoroutine { continuation ->

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                continuation.resume(ImageState.Error)
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                if (bitmap == null) continuation.resume(ImageState.Error)
                else {
                    // Really small images are loaded without filtering to retain pixel perfect sharpness
                    val isPixelatedIcon =
                        bitmap.height <= minBitmapSizePx || bitmap.width <= minBitmapSizePx

                    val image = Bitmap.createScaledBitmap(
                        bitmap,
                        targetWidthPx,
                        targetHeightPx,
                        !isPixelatedIcon
                    ).asImageAsset()

                    continuation.resume(ImageState.ImageSuccess(image))
                }
            }
        }

        val picasso = Picasso.get()

        picasso.load(imageUrl)
            .transform(CropSquareTransformation)
            .into(target)

        continuation.invokeOnCancellation {
            picasso.cancelRequest(target)
        }
    }

object CropSquareTransformation : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size = min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val result = Bitmap.createBitmap(source, x, y, size, size)
        if (result != source) {
            source.recycle()
        }
        return result
    }

    override fun key(): String = "square()"
}

@Composable
private fun DrawImageState(state: ImageState, fallbackIcon: @Composable (() -> Unit)?) {
    when (state) {
        is ImageState.Loading ->
            Stack(modifier = Modifier.fillMaxSize()) {
                ShimmerView()
                fallbackIcon?.invoke()
            }
        is ImageState.Error -> {
            fallbackIcon?.invoke()
        }
        is ImageState.ImageSuccess ->
            Image(asset = state.image)
    }
}

internal sealed class ImageState {
    data class ImageSuccess(val image: ImageAsset) : ImageState()
    object Loading : ImageState()
    object Error : ImageState()
}