package com.android.myaudioplayer.presentation.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.myaudioplayer.R
import com.android.myaudioplayer.presentation.screens.AudioData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MusicItem(
    audioData: AudioData, context: Context,
    onItemClick: (AudioData) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(2.dp)
            .clickable {
                onItemClick(audioData)
            },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(5.dp)
        ) {
            Image(
                painter = if (audioData.albumData != null) getImagePainter(
                    context = context,
                    bitMap  = audioData.albumData
                )
                else {
                    painterResource(id = R.drawable.music)
                },
                contentDescription = "",
                modifier = Modifier.size(60.dp).clip(CircleShape),
                contentScale = ContentScale.FillBounds
            )
            Text(
                modifier = Modifier.padding(horizontal = 5.dp),
                text = audioData.title, fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

suspend fun getAlbumArt(uri: String): Bitmap? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(uri)
    val art = withContext(Dispatchers.IO) {
        retriever.embeddedPicture?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }
    retriever.release()
    return art
}