package com.android.myaudioplayer.presentation

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.android.myaudioplayer.presentation.navigation.Destinations
import com.android.myaudioplayer.presentation.screens.AudioData

object Constants {
    var Destination= Destinations.SPLASH_SCREEN
    var isOpenFromNotification=false
    var audioList=mutableStateOf(listOf<AudioData>())
    var favAudioList=mutableStateOf(listOf<AudioData>())
    var RecentPlayList=mutableStateOf(listOf<AudioData>())
}