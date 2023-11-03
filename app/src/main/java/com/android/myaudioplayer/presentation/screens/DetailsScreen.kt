package com.android.myaudioplayer.presentation.screens

import android.app.Activity
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.android.myaudioplayer.MainActivity
import com.android.myaudioplayer.MediaPlayerViewModel
import com.android.myaudioplayer.R
import com.android.myaudioplayer.presentation.components.getImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class, ExperimentalStdlibApi::class)
@Composable
fun MusicDetailsScreen(mediaPlayerViewModel:MediaPlayerViewModel) {
    val context = LocalContext.current
    val mediaService = (context as Activity as MainActivity)
    val mediaPlayerService = mediaPlayerViewModel.mediaPlayerService!!
    // Display current position and total duration
    val currentPosition = mediaPlayerService.getCurrentPosition()
    val duration = mediaPlayerService.getDuration()
    val scope = rememberCoroutineScope()
/*    val showScreen = rememberSaveable {
        mutableStateOf(false)
    }*/
/*    LaunchedEffect(key1 = !showScreen.value) {
//        delay(1000)
        showScreen.value = true
    }*/
    val pagerState = rememberPagerState(
        pageCount = mediaPlayerService.audioList.value.size,
        initialPage = mediaPlayerService.currentMusicPosition.value
    )
    LaunchedEffect(key1 = pagerState.currentPage) {
        if (mediaPlayerService.currentMusicPosition.value != pagerState.currentPage) {
            mediaPlayerService.currentMusicPosition.value = pagerState.currentPage
            mediaPlayerService.isPlaying.value = false
            mediaPlayerService.manuallyPaused.value = false
            mediaPlayerService.selectedAudioFile?.value =
                mediaPlayerService.audioList.value[pagerState.currentPage]
        }
    }
    val selectedSong = mediaPlayerService.selectedAudioFile?.value
    LaunchedEffect(key1 = mediaPlayerService.selectedAudioFile?.value) {
        if (!mediaPlayerService.isPlaying.value && !mediaPlayerService.manuallyPaused.value) {
            if (mediaPlayerService.mPlayer != null) {
                mediaPlayerService.stopPlaying()
                mediaPlayerService.selectedAudioFile?.value?.let {
                    val uri = Uri.parse(it.uri)
                    mediaPlayerService.setMediaUri(uri)
                    mediaPlayerService.play()
                    pagerState.animateScrollToPage(
                        mediaPlayerService.currentMusicPosition.value,
                        animationSpec = tween(1000)
                    )
                }
            } else {
                pagerState.animateScrollToPage(mediaPlayerService.currentMusicPosition.value)
                mediaPlayerService.selectedAudioFile?.value?.let {
                    val uri = Uri.parse(it.uri)
                    mediaPlayerService.setMediaUri(uri)
                    mediaPlayerService.play()
                    pagerState.animateScrollToPage(mediaPlayerService.currentMusicPosition.value)
                }
            }
        }
    }
/*    AnimatedVisibility(visible = showScreen.value,
        enter = slideInVertically(tween(2000), initialOffsetY = {2000}),
        exit = fadeOut(tween(2000))
    ) {*/
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 20.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "",
                        alignment = Alignment.CenterStart,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                    )
                    Text(
                        text = "Now Playing",
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Image(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "",
                        alignment = Alignment.CenterEnd,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth(), itemSpacing = 10.dp,

                        ) { page ->
                        // Our page content
                        Card(
                            elevation = CardDefaults.cardElevation(10.dp),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.graphicsLayer {
                                val pageOffSet = calculateCurrentOffsetForPage(page).absoluteValue
                                lerp(
                                    start = 0.70f,
                                    stop = 1f,
                                    fraction = 1f - pageOffSet.coerceIn(0f, 1f)
                                ).also { scale ->
                                    scaleX = scale
                                    scaleY = scale
                                }
                            }
                        ) {
                            Image(
                                painter = if (mediaPlayerService.audioList.value[page].albumData != null) getImagePainter(
                                    context = context,
                                    bitMap = mediaPlayerService.audioList.value[page].albumData
                                ) else {
                                    painterResource(id = R.drawable.music)
                                },
                                contentDescription = "",
                                modifier = Modifier
                                    .width(250.dp)
                                    .height(400.dp),
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedSong?.artist ?: "",
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
                            text = selectedSong?.title ?: "",
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.basicMarquee()
                        )
                    }
                    Slider(
                        value = mediaPlayerService.progress.value,
                        onValueChange = { newValue ->
                            mediaPlayerService.pause()
                            val newPosition =
                                (newValue * mediaPlayerService.mPlayer?.duration?.toFloat()!!).toInt()
                            mediaPlayerService.seekToPosition(newPosition)
                        },
                        onValueChangeFinished = {
                            if (!mediaPlayerService.manuallyPaused.value) {
                                mediaPlayerService.play()
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
                            text = currentPosition, color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = duration, color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            scope.launch {
                                if (mediaPlayerService.currentMusicPosition.value in 1 until mediaPlayerService.audioList.value.size) {
                                    mediaPlayerService.isPlaying.value = false
                                    mediaPlayerService.manuallyPaused.value = false
                                    mediaPlayerService.currentMusicPosition.value -= 1
                                    mediaPlayerService.selectedAudioFile?.value =
                                        mediaPlayerService.audioList.value[mediaPlayerService.currentMusicPosition.value]
                                }
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.previous),
                                contentDescription = "",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(50.dp))
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
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(50.dp))
                        IconButton(onClick = {
                            if (mediaPlayerService.currentMusicPosition.value in 0 until mediaPlayerService.audioList.value.size - 1) {
                                mediaPlayerService.isPlaying.value = false
                                mediaPlayerService.manuallyPaused.value = false
                                mediaPlayerService.currentMusicPosition.value += 1
                                mediaPlayerService.selectedAudioFile?.value =
                                    mediaPlayerService.audioList.value[mediaPlayerService.currentMusicPosition.value]
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.next_button),
                                contentDescription = "",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
//    }
}


