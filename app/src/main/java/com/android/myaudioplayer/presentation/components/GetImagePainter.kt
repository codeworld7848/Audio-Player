package com.android.myaudioplayer.presentation.components

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun getImagePainter(
    context: Context,
    bitMap:Bitmap?
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context = context)
            .data(bitMap)
            .decoderFactory(SvgDecoder.Factory())
            .build()
    )
}
