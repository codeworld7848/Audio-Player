package com.android.myaudioplayer.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.myaudioplayer.MainActivity
import com.android.myaudioplayer.MediaPlayerService
import com.android.myaudioplayer.MediaPlayerViewModel
import com.android.myaudioplayer.R
import com.android.myaudioplayer.presentation.Constants
import com.android.myaudioplayer.presentation.components.CustomBottomBar
import com.android.myaudioplayer.presentation.components.FavMusicItem
import com.android.myaudioplayer.presentation.components.MusicItem
import com.android.myaudioplayer.presentation.components.getImagePainter
import com.android.myaudioplayer.presentation.navigation.Destinations
import com.android.myaudioplayer.utils.Utils
import kotlinx.coroutines.delay
import java.util.Random

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun SongsScreen(
    navController: NavController,
    mediaPlayerViewModel: MediaPlayerViewModel
) {
    val context = LocalContext.current
    val mediaService = (context as Activity as MainActivity)
    val mediaPlayerService = mediaPlayerViewModel.mediaPlayerService!!
    LaunchedEffect(key1 = mediaPlayerService.selectedAudioFile?.value) {
        mediaPlayerService.selectedAudioFile?.value?.let { audioData ->
            if (!mediaPlayerService.isPlaying.value && !mediaPlayerService.manuallyPaused.value) {
                if (mediaPlayerService.mPlayer != null) {
                    mediaPlayerService.stopPlaying()
                }
                val intent = Intent(context, MediaPlayerService::class.java)
                intent.action = "ACTION_PLAY"
                val uri = Uri.parse(audioData.uri)
                intent.data = uri
                intent.putExtra("albumImage", audioData.albumData.toString())
                context.startService(intent)
                Utils.addRecentPlayedPreference(context, audioData)

                Constants.RecentPlayList.value = Utils.getRecentPlayedPreference(context)
            }
        }
    }
    val showScreen = rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = !showScreen.value) {
        delay(1000)
        showScreen.value = true
    }

    //Get the fav music list
    LaunchedEffect(key1 = true) {
        Constants.favAudioList.value = Utils.getFavPreference(context)
        Constants.RecentPlayList.value = Utils.getRecentPlayedPreference(context)
    }
    LaunchedEffect(key1 = mediaPlayerService.audioList.value) {
        if (Constants.audioList.value.isEmpty()) {
            Constants.audioList.value = mediaPlayerService.audioList.value
        }
    }
    val isLongClicked = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedAudioFIle: MutableState<ArrayList<Int>> = remember {
        mutableStateOf(arrayListOf())
    }

    BackHandler {
        if (isLongClicked.value) {
            isLongClicked.value = !isLongClicked.value
            selectedAudioFIle.value.clear()
        } else {
            navController.popBackStack()
        }
    }

    if (!showScreen.value) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary)
        }
    } else {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.background(MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val selected = remember {
                    mutableStateOf(0)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    if (Constants.RecentPlayList.value.isNotEmpty()) {
                        Text(
                            text = "Recently Played..",
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                        val state = rememberLazyGridState()
                        LazyHorizontalGrid(
                            state = state,
                            rows = GridCells.Fixed(1),
                            modifier = Modifier
                                .height(180.dp)
                                .padding(bottom = 10.dp),
                            contentPadding = PaddingValues(end = 5.dp)
                        ) {
                            itemsIndexed(Constants.RecentPlayList.value) { position, audioData ->
                                FavMusicItem(
                                    audioData = audioData,
                                    context = context,
                                    onItemClick = {
                                        mediaPlayerService.isPlaying.value = false
                                        mediaPlayerService.manuallyPaused.value = false
                                        mediaPlayerService.selectedAudioFile?.value = it
                                        mediaPlayerService.currentMusicPosition.value = position
                                        mediaPlayerService.itemClicked.value = true
                                    },
                                    isLongClicked,
                                    longPressedSelectedItemClicked = {
                                        if (selectedAudioFIle.value.contains(position)) {
                                            selectedAudioFIle.value.remove(position)
                                        } else {
                                            selectedAudioFIle.value.add(position)
                                        }
                                        selected.value = Random().nextInt()
                                    }
                                )
                            }
                        }
                    }
                    Text(
                        text = "All Songs..",
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )

                    if (mediaPlayerService.searchedMusic.value.isEmpty()) {
                        val lazyState = rememberLazyListState()
                        LazyColumn(
                            state = lazyState,
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyState)
                        ) {
                            itemsIndexed(Constants.audioList.value) { position, audioData ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isLongClicked.value) {
                                        selected.value.apply {
                                            Checkbox(
                                                checked = selectedAudioFIle.value.contains(position),
                                                onCheckedChange = {
                                                    if (selectedAudioFIle.value.contains(position)) {
                                                        selectedAudioFIle.value.remove(position)
                                                    } else {
                                                        selectedAudioFIle.value.add(position)
                                                    }
                                                    selected.value = Random().nextInt()
                                                })
                                        }
                                    }
                                    MusicItem(
                                        audioData = audioData,
                                        context = context,
                                        onItemClick = {
                                            mediaPlayerService.isPlaying.value = false
                                            mediaPlayerService.manuallyPaused.value = false
                                            mediaPlayerService.selectedAudioFile?.value = it
                                            mediaPlayerService.currentMusicPosition.value = position
                                            mediaPlayerService.itemClicked.value = true
                                        },
                                        isLongClicked,
                                        longPressedSelectedItemClicked = {
                                            if (selectedAudioFIle.value.contains(position)) {
                                                selectedAudioFIle.value.remove(position)
                                            } else {
                                                selectedAudioFIle.value.add(position)
                                            }
                                            selected.value = Random().nextInt()
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        val filteredMusicList = ArrayList<AudioData>()
                        for (audio in Constants.audioList.value) {
                            if (audio.title.lowercase()
                                    .contains(mediaPlayerService.searchedMusic.value.lowercase())
                            ) {
                                filteredMusicList.add(audio)
                            }
                        }
                        val lazyState = rememberLazyListState()
                        LazyColumn(
                            state = lazyState,
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyState)
                        ) {
                            itemsIndexed(filteredMusicList) { position, audioData ->
                                MusicItem(
                                    audioData = audioData, context = context,
                                    onItemClick = {
                                        mediaPlayerService.isPlaying.value = false
                                        mediaPlayerService.manuallyPaused.value = false
                                        mediaPlayerService.selectedAudioFile?.value = it
                                        mediaPlayerService.currentMusicPosition.value = position
                                        mediaPlayerService.itemClicked.value = true
                                    },
                                    isLongClicked = isLongClicked,
                                    longPressedSelectedItemClicked = {
                                        selectedAudioFIle.value.add(position)
                                    }
                                )
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    visible = mediaPlayerService.itemClicked.value && !isLongClicked.value,
                    modifier = Modifier.background(Color.White)
                ) {
                    Card(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .clickable {
                                navController.navigate(Destinations.DETAILS_SCREEN_ROUTE) {
                                    launchSingleTop = true
                                    this.restoreState
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
                AnimatedVisibility(
                    visible = isLongClicked.value,
                    modifier = Modifier.background(Color.White)
                ) {
                    CustomBottomBar(
                        isEnable = selected.value.let {
                            selectedAudioFIle.value.isNotEmpty()
                        },
                        playlistBtnClicked = {

                        },
                        favClicked = {
                            val selectedFavAudioList: ArrayList<AudioData> = arrayListOf()
                            selectedAudioFIle.value.forEach {
                                selectedFavAudioList.add(mediaPlayerService.audioList.value[it])
                            }
                            selectedFavAudioList.forEach {
                                Log.d("SelectedFavSong", it.title.toString())
                            }
                            Utils.addToFavPreference(context, selectedFavAudioList)
                            Constants.favAudioList.value=Utils.getFavPreference(context)
                        },
                        deleteClicked = {
                            val deletedAudioList: ArrayList<AudioData> = arrayListOf()
                            selectedAudioFIle.value.forEach {
                                deletedAudioList.add(mediaPlayerService.audioList.value[it])
                            }
                            deletedAudioList.forEach {
                                Log.d("DeletedSong", it.title.toString())
                            }
                            Utils.deleteSongs(deletedAudioList, context)
                        },
                        shareClicked = {

                        }
                    )
                }
            }
        }
    }
}

