package com.android.myaudioplayer.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.android.myaudioplayer.MediaPlayerViewModel
import com.android.myaudioplayer.presentation.Constants
import com.android.myaudioplayer.presentation.components.CustomTopBar
import com.android.myaudioplayer.presentation.navigation.Destinations
import com.android.myaudioplayer.utils.Utils

@Composable
fun HomeScreen(navController: NavController, mediaPlayerViewModel: MediaPlayerViewModel) {
    val searchIconClicked = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val mediaPlayerService = mediaPlayerViewModel.mediaPlayerService!!
    LaunchedEffect(key1 = true) {
        if (mediaPlayerService.audioList.value.isEmpty()) {
            mediaPlayerService.getSongsFromDevice(context)
        }
    }
    //Get the fav music list
    LaunchedEffect(key1 = true) {
        Constants.favAudioList.value = Utils.getFavPreference(context)
    }

    Scaffold(
        topBar = {
            CustomTopBar(navController,searchIconClicked,mediaPlayerViewModel=mediaPlayerViewModel){
                navController.navigate(Destinations.FAV_SCREEN_ROUTE){
                    launchSingleTop=true
                }
            }
        }
    ) {
        Column(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            SongsScreen(navController,mediaPlayerViewModel)
        }
    }
}

data class AudioData(
    val path: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: String,
    val uri: String,
    val albumData: String?
)


