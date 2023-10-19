package com.android.myaudioplayer.presentation

import android.media.MediaPlayer
import android.net.Uri
import com.android.myaudioplayer.presentation.navigation.Destinations
import com.android.myaudioplayer.presentation.screens.AudioData

object Constants {
    var image: ByteArray? = null
    var authorName: String = ""
    var audioName: String = ""
    var songUri: Uri? = null
    var Destination= Destinations.HOME_SCREEN_ROUTE
    var isOpenFromNotification=false
}