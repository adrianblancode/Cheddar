package co.adrianblan.ui.coil

import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder

class CoilImageLoaderFactory(private val context: Context) : ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
                add(IcoDecoder.Factory())
            }
            .build()
    }
}