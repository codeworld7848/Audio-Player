package com.android.myaudioplayer.presentation.components

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.android.myaudioplayer.R

@Composable
fun getImagePainter(
    context: Context,
    bitMap:String?
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context = context)
            .data(bitMap)
            .decoderFactory(SvgDecoder.Factory())
            .error(R.drawable.music)
            .build(),
        placeholder = painterResource(id = R.drawable.music),
    )
}
