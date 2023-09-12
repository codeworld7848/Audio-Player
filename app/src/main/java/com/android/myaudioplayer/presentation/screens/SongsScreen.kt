package com.android.myaudioplayer.presentation.screens

import android.annotation.SuppressLint
import android.media.MediaPlayer
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.android.myaudioplayer.MediaPlayerViewModel
import com.android.myaudioplayer.R
import com.android.myaudioplayer.presentation.components.MusicItem
import com.android.myaudioplayer.presentation.components.getImagePainter
import com.android.myaudioplayer.presentation.navigation.Destinations
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun SongsScreen(
    navController: NavController,
    mediaPlayerViewModel: MediaPlayerViewModel
) {
    val context = LocalContext.current
    val itemClicked = rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = mediaPlayerViewModel.selectedAudioFile?.value) {
        if (!mediaPlayerViewModel.isPlaying.value && !mediaPlayerViewModel.manuallyPaused.value) {
            if (mediaPlayerViewModel.mediaPlayer != null) {
                mediaPlayerViewModel.mediaPlayer?.let {
                    it.stop()
                    it.release()
                    mediaPlayerViewModel.selectedAudioFile?.value?.uri?.let { uri ->
                        mediaPlayerViewModel.mediaPlayer = MediaPlayer.create(context, uri)
                        mediaPlayerViewModel.playMusic()
                    }
                }
            } else {
                mediaPlayerViewModel.selectedAudioFile?.value?.uri?.let { uri ->
                    mediaPlayerViewModel.mediaPlayer = MediaPlayer.create(context, uri)
                    mediaPlayerViewModel.playMusic()
                }
            }
        }
    }
    // Observe playback progress and update the slider
    LaunchedEffect(mediaPlayerViewModel) {
        while (true) {
            mediaPlayerViewModel.updateProgress(){}
            delay(1000) // Update progress every second (adjust as needed)
        }
    }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    ) {

        if (mediaPlayerViewModel.audioList.value.isEmpty()) {
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
                if (mediaPlayerViewModel.searchedMusic.value.isEmpty()) {
                    itemsIndexed(mediaPlayerViewModel.audioList.value) { position, audioData ->
                        MusicItem(audioData = audioData, context = context, onItemClick = {
                            mediaPlayerViewModel.isPlaying.value = false
                            mediaPlayerViewModel.manuallyPaused.value = false
                            mediaPlayerViewModel.selectedAudioFile?.value = it
                            mediaPlayerViewModel.currentMusicPosition.value = position
                            itemClicked.value = true
                        })
                    }
                } else {
                    val filteredMusicList = ArrayList<AudioData>()
                    for (audio in mediaPlayerViewModel.audioList.value) {
                        if (audio.title.lowercase()
                                .contains(mediaPlayerViewModel.searchedMusic.value.lowercase())
                        ) {
                            filteredMusicList.add(audio)
                        }
                    }
                    itemsIndexed(filteredMusicList) { position, audioData ->
                        MusicItem(audioData = audioData, context = context, onItemClick = {
                            mediaPlayerViewModel.isPlaying.value = false
                            mediaPlayerViewModel.manuallyPaused.value = false
                            mediaPlayerViewModel.selectedAudioFile?.value = it
                            mediaPlayerViewModel.currentMusicPosition.value = position
                            itemClicked.value = true
                        })
                    }
                }
            }
            AnimatedVisibility(
                visible = itemClicked.value,
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
                            painter = if (mediaPlayerViewModel.selectedAudioFile?.value?.albumData != null) getImagePainter(
                                context = context,
                                bitMap = mediaPlayerViewModel.selectedAudioFile?.value?.albumData
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
                                text = mediaPlayerViewModel.selectedAudioFile?.value?.title
                                    ?: "",
                                color = Color.White,
                                modifier = Modifier.basicMarquee()
                            )
                            Text(
                                text = mediaPlayerViewModel.selectedAudioFile?.value?.artist
                                    ?: "",
                                color = Color.White
                            )
                        }
                        IconButton(onClick = {
                            if (mediaPlayerViewModel.mediaPlayer?.isPlaying == true) {
                                mediaPlayerViewModel.manuallyPaused.value = true
                                mediaPlayerViewModel.pauseMusic()
                            } else {
                                mediaPlayerViewModel.playMusic()
                            }
                        }) {
                            Icon(
                                painter = painterResource(
                                    id = if (mediaPlayerViewModel.isPlaying.value) {
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