package co.adrianblan.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.*
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Image
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.fillMaxSize
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val minBitmapSizePx = 48

/** Takes an image url and loads it with Picasso */
@Composable
fun UrlImage(
    imageUrl: String,
    height: Dp = 80.dp,
    width: Dp = 80.dp
) {

    val targetWidthPx = with(DensityAmbient.current) { remember { width.toIntPx() } }
    val targetHeightPx = with(DensityAmbient.current) { remember { height.toIntPx() } }

    var imageState by stateFor<ImageState, String>(imageUrl) { ImageState.Loading }

    launchInComposition(imageUrl) {
        // State will not cause recomposition if changed in the same frame as creation, work around this
        delay(1)

        imageState = loadImage(imageUrl, targetWidthPx, targetHeightPx)
    }

    DrawImageState(state = imageState)
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

        picasso
            .load(imageUrl)
            .into(target)

        continuation.invokeOnCancellation {
            picasso.cancelRequest(target)
        }
    }

@Composable
private fun DrawImageState(state: ImageState) {
    when (state) {
        is ImageState.Loading ->
            Box(modifier = Modifier.fillMaxSize()) {
                ShimmerView()
            }
        is ImageState.Error -> {
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