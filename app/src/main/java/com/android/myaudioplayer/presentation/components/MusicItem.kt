package com.android.myaudioplayer.presentation.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.DefaultMarqueeIterations
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.android.myaudioplayer.R
import com.android.myaudioplayer.presentation.screens.AudioData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicItem(
    audioData: AudioData,
    context: Context,
    onItemClick: (AudioData) -> Unit,
    isLongClicked: MutableState<Boolean>,
    longPressedSelectedItemClicked: () -> Unit
) {
    val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)

    Card(
        modifier = Modifier
            .padding(2.dp)
            .combinedClickable(
                onClick = {
                    if (isLongClicked.value) {
                        longPressedSelectedItemClicked.invoke()
                    } else {
                        onItemClick(audioData)
                    }
                },
                onLongClick = {
                    val vibrationEffect1: VibrationEffect =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                        } else {
                            Log.e("TAG", "Cannot vibrate device..")
                            TODO("VERSION.SDK_INT < O")
                        }

                    // it is safe to cancel other
                    // vibrations currently taking place
                    vibrator?.cancel()
                    vibrator?.vibrate(vibrationEffect1)
                    isLongClicked.value = true
                    longPressedSelectedItemClicked.invoke()
                }
            ),
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
                    bitMap = audioData.albumData
                )
                else {
                    painterResource(id = R.drawable.music)
                },
                contentDescription = "",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavMusicItem(
    audioData: AudioData,
    context: Context,
    onItemClick: (AudioData) -> Unit,
    isLongClicked: MutableState<Boolean>,
    longPressedSelectedItemClicked: () -> Unit
) {
    val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)

    Card(
        modifier = Modifier
            .padding(2.dp)
            .width(150.dp)
            .height(180.dp)
            .combinedClickable(
                onClick = {
                    if (isLongClicked.value) {
                        longPressedSelectedItemClicked.invoke()
                    } else {
                        onItemClick(audioData)
                    }
                },
                onLongClick = {
                    val vibrationEffect1: VibrationEffect =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                        } else {
                            Log.e("TAG", "Cannot vibrate device..")
                            TODO("VERSION.SDK_INT < O")
                        }

                    // it is safe to cancel other
                    // vibrations currently taking place
                    vibrator?.cancel()
                    vibrator?.vibrate(vibrationEffect1)
                    isLongClicked.value = true
                    longPressedSelectedItemClicked.invoke()
                }
            ),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentHeight()
                .padding(5.dp)
        ) {
            Image(
                painter = if (audioData.albumData != null) getImagePainter(
                    context = context,
                    bitMap = audioData.albumData
                )
                else {
                    painterResource(id = R.drawable.music)
                },
                contentDescription = "",
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.FillBounds
            )
            Text(
                modifier = Modifier.padding(horizontal = 5.dp)
                    .basicMarquee(
                        iterations = Int.MAX_VALUE
                    ),
                text = audioData.title, fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}