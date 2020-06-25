package co.adrianblan.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.*
import androidx.ui.core.*
import androidx.ui.foundation.Box
import androidx.ui.foundation.Image
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.*
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
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

    val imageState = stateFor(imageUrl) { ImageState.Loading as ImageState }

    val targetWidthPx = with(DensityAmbient.current) { remember { width.toIntPx() } }
    val targetHeightPx = with(DensityAmbient.current) { remember { height.toIntPx() } }

    launchInComposition(imageUrl) {
        suspendCancellableCoroutine { continuation ->

            val target = object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    imageState.value = ImageState.Error
                    continuation.resume(Unit)
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                    imageState.value =
                        if (bitmap == null) ImageState.Error
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

                            ImageState.ImageSuccess(image)
                        }

                    continuation.resume(Unit)
                }
            }

            val picasso = Picasso.get()

            picasso
                .load(imageUrl)
                .into(target)

            continuation.invokeOnCancellation {
                imageState.value = ImageState.Loading
                picasso.cancelRequest(target)
            }
        }
    }

    DrawImageState(state = imageState.value)
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