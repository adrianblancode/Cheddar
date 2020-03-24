package co.adrianblan.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.*
import androidx.ui.core.*
import androidx.ui.foundation.DrawImage
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutWidth
import androidx.ui.material.ColorPalette
import androidx.ui.material.surface.Surface
import androidx.ui.res.colorResource
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import timber.log.Timber

/** Takes an image url and loads it with Picasso */
@Composable
fun UrlImage(
    imageUrl: String,
    height: Dp = 80.dp,
    width: Dp = 80.dp
) {

    val imageState = stateFor<ImageState, String>(imageUrl) {
        ImageState.Loading
    }

    val targetWidthPx = with(DensityAmbient.current) { width.toIntPx().value }
    val targetHeightPx = with(DensityAmbient.current) { height.toIntPx().value }

    onCommit(imageUrl) {

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                imageState.value = ImageState.Loading
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                imageState.value = ImageState.Error
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                imageState.value =
                    if (bitmap == null) ImageState.Error
                    else {
                        val image =
                            // Really small images are loaded without filtering to retain pixel perfect sharpness
                            if (bitmap.height <= 48 || bitmap.width <= 48) {
                                Bitmap.createScaledBitmap(
                                    bitmap,
                                    targetWidthPx,
                                    targetHeightPx,
                                    false
                                ).asImageAsset()
                            } else bitmap.asImageAsset()

                        ImageState.ImageSuccess(image)
                    }
            }
        }

        val picasso = Picasso.get()

        picasso
            .load(imageUrl)
            .into(target)

        onDispose {
            imageState.value = ImageState.Loading
            picasso.cancelRequest(target)
        }
    }

    val modifier: Modifier = LayoutWidth(width) + LayoutHeight(height)
    DrawImageState(state = imageState.value, modifier = modifier)
}

@Composable
private fun DrawImageState(state: ImageState, modifier: Modifier) {
    RepaintBoundary {
        when (state) {
            is ImageState.Loading ->
                Container(expanded = true) {
                    ShimmerView()
                }
            is ImageState.Error ->
                Surface(
                    color = colorResource(id = R.color.contentMuted),
                    modifier = modifier
                ) {}
            is ImageState.ImageSuccess ->
                DrawImage(image = state.image)
        }
    }
}

private sealed class ImageState {
    object Loading : ImageState()
    object Error : ImageState()
    class ImageSuccess(val image: Image) : ImageState()
}