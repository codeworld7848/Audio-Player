package com.android.myaudioplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.myaudioplayer.presentation.components.getImagePainter
import com.android.myaudioplayer.presentation.screens.AudioData
import com.android.myaudioplayer.ui.theme.MyAudioPlayerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AudioPlayerActivity : ComponentActivity() {
    val mediaPlayerViewModel: MediaPlayerViewModel by viewModels()
    var mediaPlayerService: MediaPlayerService? = null
    var mBound = false
    val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            val mServiceBinder = iBinder as MediaPlayerService.MyMusicServiceBinder
            mediaPlayerService = mServiceBinder.getService()
            mBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAudioPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    val intent = Intent(this, MediaPlayerService::class.java)
                    bindService(intent, mServiceConnection, BIND_AUTO_CREATE)
                    val context = LocalContext.current
//                    val context = LocalContext.current
                    LaunchedEffect(key1 = true) {
                        val uri = intent.data
                        if (uri != null) {
//                            mediaPlayerViewModel.selectedAudioFile?.value =
//                                extractAudioInfo(uri = uri)
                        }
                    }
                    val selectedSong = mediaPlayerViewModel.selectedAudioFile?.value
                    LaunchedEffect(key1 = mediaPlayerViewModel.selectedAudioFile?.value) {
                        if (!mediaPlayerViewModel.isPlaying.value && !mediaPlayerViewModel.manuallyPaused.value) {
                            if (mediaPlayerViewModel.mediaPlayer != null) {
                                mediaPlayerViewModel.mediaPlayer?.let {
                                    it.stop()
                                    it.release()
                                    selectedSong?.uri?.let { uriString ->
                                        val uri = Uri.parse(uriString)
                                        mediaPlayerViewModel.mediaPlayer =
                                            MediaPlayer.create(context, uri)
                                        mediaPlayerViewModel.playMusic()
                                    }
                                }
                            } else {
                                selectedSong?.uri?.let {uriString ->
                                    val uri = Uri.parse(uriString)
                                    mediaPlayerViewModel.mediaPlayer =
                                        MediaPlayer.create(context, uri)
                                    mediaPlayerViewModel.duration.value =
                                        mediaPlayerViewModel.getDuration()
                                    mediaPlayerViewModel.playMusic()
                                }
                            }
                        }
                    }
                    // Observe playback progress and update the slider
                    LaunchedEffect(mediaPlayerViewModel) {
                        while (true) {
                            mediaPlayerViewModel.currentPosition.value =
                                mediaPlayerViewModel.getCurrentPosition()
                            mediaPlayerViewModel.updateProgress() {}
                            delay(1000) // Update progress every second (adjust as needed)
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (mediaPlayerViewModel.selectedAudioFile?.value == null) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(200.dp),
                                color = Color.Red
                            )
                        }
                        AnimatedVisibility(
                            visible = mediaPlayerViewModel.selectedAudioFile?.value != null,
                            enter = slideInVertically(initialOffsetY = {
                                it
                            }),
                            exit = slideOutVertically(
                                targetOffsetY = { -it },
                                animationSpec = tween(durationMillis = 300)
                            )
                        ) {
                            AudioPlayingCard(
                                mediaPlayerViewModel.selectedAudioFile,
                                context,
                                mediaPlayerViewModel.currentPosition.value,
                                mediaPlayerViewModel.duration.value
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun AudioPlayingCard(
        audioData: MutableState<AudioData?>?,
        context: Context,
        currentPosition: String,
        duration: String
    ) {
        audioData?.value?.let {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(50.dp),
                    painter = getImagePainter(
                        context = context,
                        bitMap = audioData.value?.albumData
                    ),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
                Card(
                    modifier = Modifier.padding(20.dp),
                    elevation = CardDefaults.cardElevation(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Image(
                            painter = getImagePainter(
                                context = context,
                                bitMap = audioData.value?.albumData
                            ), contentDescription = "",
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .shadow(10.dp)
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Column() {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = it.artist,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    IconButton(onClick = { }) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                                //Song Name Details
                                Text(
                                    text = it.title,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.basicMarquee()
                                )
                            }
                            Slider(
                                value = mediaPlayerViewModel.progress.value,
                                onValueChange = { newValue ->
                                    mediaPlayerViewModel.pauseMusic()
                                    val newPosition =
                                        (newValue * mediaPlayerViewModel.mediaPlayer?.duration?.toFloat()!!).toInt()
                                    mediaPlayerViewModel.seekToPosition(
                                        newPosition
                                    )
                                },
                                onValueChangeFinished = {
                                    if (!mediaPlayerViewModel.manuallyPaused.value) {
                                        mediaPlayerViewModel.playMusic()
                                    }
                                },
                                valueRange = 0f..1f,
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.onPrimary,
                                    activeTrackColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentPosition,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = duration,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            IconButton(onClick = {
                                if (mediaPlayerViewModel.mediaPlayer?.isPlaying == true) {
                                    mediaPlayerViewModel.manuallyPaused.value =
                                        true
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
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

/*
    private suspend fun extractAudioInfo(uri: Uri): AudioData? {
        val retriever = MediaMetadataRetriever()

        try {
            val fileDescriptor = this.contentResolver.openFileDescriptor(uri, "r")
            if (fileDescriptor != null) {
                retriever.setDataSource(fileDescriptor.fileDescriptor)
                fileDescriptor.close()

                val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                val duration =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val path = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
                val albumArt = withContext(Dispatchers.IO) {
                    retriever.embeddedPicture?.let { bytes ->
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                }
                return AudioData(
                    path = path ?: "",
                    artist = artist ?: "",
                    duration = duration.toString(),
                    uri = uri,
                    title = title ?: "",
                    album = "",
                    albumData = albumArt
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }

        // Return default values if extraction fails
        return null
    }
*/
}

