package com.android.myaudioplayer.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.myaudioplayer.MainActivity
import com.android.myaudioplayer.MediaPlayerService
import com.android.myaudioplayer.R
import com.android.myaudioplayer.presentation.components.MusicItem
import com.android.myaudioplayer.presentation.components.getImagePainter
import com.android.myaudioplayer.presentation.navigation.Destinations
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun SongsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val mediaService = (context as Activity as MainActivity)
    val mediaPlayerService = mediaService.mediaPlayerService!!
    LaunchedEffect(key1 = mediaPlayerService.selectedAudioFile?.value) {
        mediaPlayerService.selectedAudioFile?.value?.let { audioData ->
            if (!mediaPlayerService.isPlaying.value && !mediaPlayerService.manuallyPaused.value) {
                if (mediaPlayerService.mPlayer != null) {
                    mediaPlayerService.stopPlaying()
                }
                val intent = Intent(context, MediaPlayerService::class.java)
                intent.action="ACTION_PLAY"
                intent.data = audioData.uri
                intent.putExtra("albumImage",audioData.albumData.toString())
                (context as Activity).startService(intent)
//                mediaPlayerService.setMediaUri(audioData.uri)
//                mediaPlayerService.play()
            }
        }
    }
    // Observe playback progress and update the slider
    LaunchedEffect(mediaPlayerService) {
        while (true) {
            mediaPlayerService.updateProgress() {}
            delay(1000) // Update progress every second (adjust as needed)
        }
    }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    ) {

        if (mediaPlayerService.audioList.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary)
            }
        }
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                if (mediaPlayerService.searchedMusic.value.isEmpty()) {
                    itemsIndexed(mediaPlayerService.audioList.value) { position, audioData ->
                        MusicItem(audioData = audioData, context = context, onItemClick = {
                            mediaPlayerService.isPlaying.value = false
                            mediaPlayerService.manuallyPaused.value = false
                            mediaPlayerService.selectedAudioFile?.value = it
                            mediaPlayerService.currentMusicPosition.value = position
                            mediaPlayerService.itemClicked.value = true
                        })
                    }
                } else {
                    val filteredMusicList = ArrayList<AudioData>()
                    for (audio in mediaPlayerService.audioList.value) {
                        if (audio.title.lowercase()
                                .contains(mediaPlayerService.searchedMusic.value.lowercase())
                        ) {
                            filteredMusicList.add(audio)
                        }
                    }
                    itemsIndexed(filteredMusicList) { position, audioData ->
                        MusicItem(audioData = audioData, context = context, onItemClick = {
                            mediaPlayerService.isPlaying.value = false
                            mediaPlayerService.manuallyPaused.value = false
                            mediaPlayerService.selectedAudioFile?.value = it
                            mediaPlayerService.currentMusicPosition.value = position
                            mediaPlayerService.itemClicked.value = true
                        })
                    }
                }
            }
            AnimatedVisibility(
                visible = mediaPlayerService.itemClicked.value,
                modifier = Modifier.background(Color.White)
            ) {
                Card(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .clickable {
                            navController.navigate(Destinations.DETAILS_SCREEN_ROUTE) {
                                launchSingleTop = true
                            }
                        },
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onSecondary),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                ) {
                    Row(modifier = Modifier.padding(10.dp)) {
                        Image(
                            painter = if (mediaPlayerService.selectedAudioFile?.value?.albumData != null) getImagePainter(
                                context = context,
                                bitMap = mediaPlayerService.selectedAudioFile?.value?.albumData
                            ) else {
                                painterResource(id = R.drawable.music)
                            },
                            contentDescription = "",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.FillBounds
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                        ) {
                            //Song Name Details
                            Text(
                                text = mediaPlayerService.selectedAudioFile?.value?.title
                                    ?: "",
                                color = Color.White,
                                modifier = Modifier.basicMarquee()
                            )
                            Text(
                                text = mediaPlayerService.selectedAudioFile?.value?.artist
                                    ?: "",
                                color = Color.White
                            )
                        }
                        IconButton(onClick = {
                            if (mediaPlayerService.mPlayer?.isPlaying == true) {
                                mediaPlayerService.manuallyPaused.value = true
                                mediaPlayerService.pause()
                            } else {
                                mediaPlayerService.play()
                            }
                        }) {
                            Icon(
                                painter = painterResource(
                                    id = if (mediaPlayerService.isPlaying.value && !mediaPlayerService.manuallyPaused.value) {
                                        R.drawable.pause
                                    } else R.drawable.play_button
                                ),
                                contentDescription = "",
                                modifier = Modifier.size(50.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

            }
        }
    }
}