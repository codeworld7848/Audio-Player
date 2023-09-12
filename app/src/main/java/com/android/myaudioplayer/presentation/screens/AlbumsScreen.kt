package com.android.myaudioplayer.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import com.android.myaudioplayer.MediaPlayerViewModel
import com.android.myaudioplayer.presentation.components.MusicItem

@Composable
fun AlbumsScreen(mediaPlayerViewModel: MediaPlayerViewModel) {
    val musicByAlbum = mediaPlayerViewModel.audioList.value.groupBy { it.album }
    val albumList = arrayListOf<String>()
    val musicListAlbum: ArrayList<NewAudioData?> = arrayListOf(null)
    val itemClicked = remember {
        mutableStateOf(false)
    }

    for (music in mediaPlayerViewModel.audioList.value) {
        val albumName = music.album // Replace with the album name you want to retrieve
        if (!albumList.contains(albumName.lowercase())) {
            val musicListForAlbum = musicByAlbum[albumName]
            val newAudioData = NewAudioData(albumName, musicListForAlbum)
            musicListAlbum.add(newAudioData)
            albumList.add(albumName.lowercase())
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val selectedAlbum: MutableState<List<AudioData>?> = remember {
            mutableStateOf(listOf())
        }
        LazyColumn() {
            items(musicListAlbum) {
                it?.let {
                    Text(text = it.albumName, modifier = Modifier.clickable {
                        itemClicked.value = true
                        selectedAlbum.value = it.musicListForAlbum
                    })
                }
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            selectedAlbum.value?.let {
                items(it) { audio ->
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.onSecondary)) {
                        Text(text = audio.title, color = Color.White)
                    }
                }
            }
        }
    }
}


data class NewAudioData(
    val albumName: String,
    val musicListForAlbum: List<AudioData>?
)
